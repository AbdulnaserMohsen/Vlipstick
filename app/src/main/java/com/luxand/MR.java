package com.luxand;

import android.util.Log;

import java.io.InputStream;
import java.util.Scanner;

public class MR {
    static {
        System.loadLibrary("stlport_shared");
        System.loadLibrary("fsdk");
        System.loadLibrary("MirrorRealitySDK");
    }

    public static final int MAX_FACES = 5;

    public static final int MASK_TEXTURE_SIZE = 1024;

    public static final int SHIFT_TYPE_NO = 0;
    public static final int SHIFT_TYPE_OUT = 1;
    public static final int SHIFT_TYPE_IN = 2;

    public static class TPointf {
        public float x, y;
    }

    public static class MaskFeatures {
        public TPointf features[] = new TPointf[FSDK.FSDK_FACIAL_FEATURE_COUNT];
    }

    public static int LoadMaskCoordsFromStream(InputStream stream, MaskFeatures maskCoords) {
        Scanner scanner = new Scanner(stream);
        if (!scanner.hasNextLine()) {
            return FSDK.FSDKE_BAD_FILE_FORMAT;
        }
        String line = scanner.nextLine();
        int count = Integer.parseInt(line);
        if (count != FSDK.FSDK_FACIAL_FEATURE_COUNT) {
            return FSDK.FSDKE_BAD_FILE_FORMAT;
        }
        for (int i=0; i<FSDK.FSDK_FACIAL_FEATURE_COUNT; ++i) {
            maskCoords.features[i] = new TPointf();
        }
        for (int i=0; i<FSDK.FSDK_FACIAL_FEATURE_COUNT; ++i) {
            if (!scanner.hasNextLine()) return FSDK.FSDKE_IO_ERROR;
            line = scanner.nextLine();
            String [] values = line.split(" ");
            if (values.length != 2) return FSDK.FSDKE_BAD_FILE_FORMAT;
            float x = (float) Double.parseDouble(values[0]);
            float y = (float) Double.parseDouble(values[1]);
            maskCoords.features[i].x = x;
            maskCoords.features[i].y = y;
        }
        return 0;
    }

    public static native int LoadMaskCoordsFromFile(String filename, MaskFeatures maskCoords);

    public static native int LoadMask(FSDK.HImage maskImage1, FSDK.HImage maskImage2, int maskTexture1, int maskTexture2, int [] isTexture1Created, int [] isTexture2Created);

    public static native int DrawGLScene(int facesTexture, int facesDetected, FSDK.FSDK_Features[] /*must be [MAX_FACES]*/ features, int rotationAngle90Multiplier,int shiftType, int maskTexture1, int maskTexture2, MaskFeatures maskCoords, int isTexture1Created, int isTexture2Created, int width, int height);

    public static native int ActivateLibrary(String licenseKey);

}
