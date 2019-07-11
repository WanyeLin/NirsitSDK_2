package obelab.com.sdkexample;

import android.content.res.AssetManager;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class Classifer {
    static {
        System.loadLibrary("tensorflow_inference");
        Log.d("finding","SUCCESSFUL");
    }

    static private TensorFlowInferenceInterface tf ;
    static private float[] prediction = new float[4];
    Classifer(AssetManager assets, String path){
        tf = new TensorFlowInferenceInterface(assets,path);
    }

    static float[] predict(String inputname, String outputname, int width, int height, float[] data){
        tf.feed(inputname,data,width,height);
        tf.run(new String[]{outputname});
        tf.fetch(outputname,prediction);
        return prediction;
    }


}