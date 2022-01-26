package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.captureimage;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.util.Size;
import android.view.Surface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

//import com.tool.applocker.applock.item.ItemCamera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeMap;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) //NOTE: camera 2 api was added in API level 21
public class PictureCapturingServiceImpl extends APictureCapturingService {

    private CameraDevice cameraDevice;
    private ImageReader imageReader;
    /***
     * camera ids queue.
     */
    private Queue<String> cameraIds;

    private String currentCameraId;
    private String pkgName;
    private String pkg, name;
    private boolean cameraClosed;
    private boolean canWrite;
    private String pathStore;
    private ArrayList<ItemCamera> arrCamera;
    private Activity activity;
    private String nameImage;
    /**
     * stores a sorted map of (pictureUrlOnDisk, PictureData).
     */
    private TreeMap<String, byte[]> picturesTaken;
    private PictureCapturingListener capturingListener;

    /***
     * private constructor, meant to force the use of {@link #getInstance}  method
     */
    public PictureCapturingServiceImpl(final Activity activity, String pkg, String name) {
        super(activity);
        this.activity = activity;
//        arrCamera = MyShare.getArrCamera(activity);
//        pathStore = OtherUtils.getStore(activity);
//        canWrite = OtherUtils.canWriteInMediaStore(activity);
        pkgName = activity.getPackageName();
        this.pkg = pkg;
        this.name = name;
        nameImage = System.currentTimeMillis() + "_p.jpg";
    }

    /**
     * @param activity the activity used to get the app's context and the display manager
     * @return a new instance
     */
    public static APictureCapturingService getInstance(final Activity activity, String pkg, String name) {
        return new PictureCapturingServiceImpl(activity, pkg, name);
    }

    /**
     * Starts pictures capturing treatment.
     *
     * @param listener picture capturing listener
     */
    @Override
    public void startCapturing(final PictureCapturingListener listener) {
        this.picturesTaken = new TreeMap<>();
        this.capturingListener = listener;
        this.cameraIds = new LinkedList<>();
        try {
            final String[] cameraIds = manager.getCameraIdList();
            ArrayList<String> str = new ArrayList<>();
            for (String cameraId : cameraIds) {
                if (cameraId.equals("1"))
                    str.add(cameraId);
            }
            if (cameraIds.length > 0) {
                this.cameraIds.addAll(str);
                this.currentCameraId = this.cameraIds.poll();
                openCamera();
            } else {
                //No camera detected!
                capturingListener.onDoneCapturingAllPhotos(picturesTaken);
            }
        } catch (final CameraAccessException ignored) {
        }
    }

    private void openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED && canWrite) {
                manager.openCamera(currentCameraId, stateCallback, null);
            }
        } catch (final CameraAccessException ignored) {
        }
    }

    private final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            if (picturesTaken.lastEntry() != null
                    && pkg != null && !pkg.isEmpty()) {
                capturingListener.onCaptureDone(picturesTaken.lastEntry().getKey(), picturesTaken.lastEntry().getValue());
            }
            closeCamera();
        }
    };


    private final ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader imageReader) {
            final Image image = imageReader.acquireLatestImage();
            final ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            final byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            if (pkg != null && !pkg.isEmpty())
                saveImageToDisk(bytes);
            image.close();
        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraClosed = false;
            cameraDevice = camera;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        takePicture();
                    } catch (final CameraAccessException ignored) {
                    }
                }
            }, 500);
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            if (cameraDevice != null && !cameraClosed) {
                cameraClosed = true;
                cameraDevice.close();
            }
        }

        @Override
        public void onClosed(@NonNull CameraDevice camera) {
            cameraClosed = true;
            if (!cameraIds.isEmpty()) {
                takeAnotherPicture();
            } else {
                capturingListener.onDoneCapturingAllPhotos(picturesTaken);
            }
        }


        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            if (cameraDevice != null && !cameraClosed) {
                cameraDevice.close();
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void takePicture() throws CameraAccessException {
        if (null == cameraDevice) {
            return;
        }
        final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
        Size[] jpegSizes = null;
        StreamConfigurationMap streamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (streamConfigurationMap != null) {
            jpegSizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
        }
        final boolean jpegSizesNotEmpty = jpegSizes != null && 0 < jpegSizes.length;
        int width = jpegSizesNotEmpty ? jpegSizes[0].getWidth() : 640;
        int height = jpegSizesNotEmpty ? jpegSizes[0].getHeight() : 480;
        final ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
        final List<Surface> outputSurfaces = new ArrayList<>();
        outputSurfaces.add(reader.getSurface());
        final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        captureBuilder.addTarget(reader.getSurface());
        captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation());
        reader.setOnImageAvailableListener(onImageAvailableListener, null);
        cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        try {
                            session.capture(captureBuilder.build(), captureListener, null);
                        } catch (CameraAccessException ignored) {
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    }
                }
                , null);
    }


    private void saveImageToDisk(final byte[] bytes) {
        final String path = pathStore + "/cm/";
        File f = new File(path);
        if (!f.exists())
            f.mkdirs();
        ItemCamera itemCamera = new ItemCamera(path + nameImage, this.name, pkg, System.currentTimeMillis());
        if (arrCamera == null)
            arrCamera = new ArrayList<ItemCamera>();
        arrCamera.add(itemCamera);
//        MyShare.putArrCamera(activity, arrCamera);
        this.picturesTaken.put(path + nameImage, bytes);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final File file = new File(path + nameImage);
                try {
                    file.createNewFile();
                    OutputStream output = new FileOutputStream(file);
                    output.write(bytes);
                    output.close();
                    Toast.makeText(activity.getApplicationContext(),path+nameImage,Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void takeAnotherPicture() {
        this.currentCameraId = this.cameraIds.poll();
        openCamera();
    }

    private void closeCamera() {
        if (null != cameraDevice && !cameraClosed) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }


}
