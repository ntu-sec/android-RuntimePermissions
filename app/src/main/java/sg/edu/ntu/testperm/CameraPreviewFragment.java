package sg.edu.ntu.testperm;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class CameraPreviewFragment extends Fragment {

    private static final String TAG = "CameraPreview";

    private static final int CAMERA_ID = 0;

    private Camera mCamera;

    public static CameraPreviewFragment newInstance() {
        return new CameraPreviewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mCamera = getCameraInstance(CAMERA_ID);
        Camera.CameraInfo cameraInfo = null;

        if (mCamera == null) {
            Toast.makeText(getActivity(), "Camera unavailable.", Toast.LENGTH_SHORT).show();
            return inflater.inflate(R.layout.fragment_rtperm_camera_unavailable, null);
        }

        cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(CAMERA_ID, cameraInfo);

        View root = inflater.inflate(R.layout.fragment_rtperm_camera, null);

        final int displayRotation = getActivity().getWindowManager().getDefaultDisplay()
                .getRotation();

        CameraPreview mPreview = new CameraPreview(getActivity(), mCamera, cameraInfo, displayRotation);
        FrameLayout preview = (FrameLayout) root.findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    public static Camera getCameraInstance(int cameraId) {
        Camera c = null;
        try {
            c = Camera.open(cameraId); // attempt to get a Camera instance
        } catch (Exception e) {
            Log.d(TAG, "Camera " + cameraId + " is not available: " + e.getMessage());
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
