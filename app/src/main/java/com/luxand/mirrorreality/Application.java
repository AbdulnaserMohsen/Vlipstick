package com.luxand.mirrorreality;

import com.luxand.FSDK;
import com.luxand.MR;

public class Application extends android.app.Application {
    public static FSDK.HTracker tracker = new FSDK.HTracker();

    @Override
    public void onCreate() {
        super.onCreate();
        int res = MR.ActivateLibrary(
                "RkanvCCmO26GNNEICaIcCSr+b2OhOiH1gAq0xOkEaSDuPOk25L91RqF5WnWtBFmHyyJn8lX6yHgjvl2GWu3yuOXFhavBm8+tUZuRYzCCXIuXqA5mt9GuyjvIee9MPuAM15rGFKdwEmaDy1GERYnWXnfqRueWB8fW5qngrWSH45h=");
        if (res != FSDK.FSDKE_OK) {
            throw new RuntimeException("Not activated");
        }
        FSDK.Initialize();

        FSDK.CreateTracker(tracker);

        int [] errorPosition = new int[1];
        FSDK.SetTrackerMultipleParameters(tracker, "DetectFaces=false;RecognizeFaces=false;DetectFacialFeatures=true;DetectEyes=false;ContinuousVideoFeed=true;ThresholdFeed=0.97;MemoryLimit=1000;HandleArbitraryRotations=false;DetermineFaceRotationAngle=false;InternalResizeWidth=70;FaceDetectionThreshold=3;FacialFeatureJitterSuppression=3;",
                errorPosition);
    }
}
