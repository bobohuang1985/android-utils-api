#include <jni.h>
#include "audio_ns.h"
#include "noise_suppression.h"


extern "C" {

NsHandle* handle = NULL;


void innerProcess(short in_sample[], short out_sample[], int length){

    int curPosition = 0;

    while(curPosition < length){

        audio_ns_process((int) handle, in_sample + curPosition, out_sample + curPosition);

        curPosition += 160;

    }

}

JNIEXPORT jboolean JNICALL
Java_com_test_jni_WebrtcProcessor_init(JNIEnv *env, jobject instance, jint sample_rate) {

    handle = (NsHandle *) audio_ns_init(sample_rate);

    return false;
}

JNIEXPORT jboolean JNICALL
Java_com_test_jni_WebrtcProcessor_processNoise(JNIEnv *env, jobject instance, jshortArray sample) {

    if(!handle)
        return false;

    jsize length = env->GetArrayLength(sample);

    jshort *sam = env->GetShortArrayElements(sample, 0);

    short in_sample[length];
    for(int i=0; i<length; i++){
        in_sample[i] = sam[i];
    }

    innerProcess(in_sample, sam, length);

    env->ReleaseShortArrayElements(sample, sam, 0);

    return true;
}

JNIEXPORT void JNICALL
Java_com_test_jni_WebrtcProcessor_release(JNIEnv *env, jobject instance) {

    if(handle){
        audio_ns_destroy((int) handle);
    }


}

}
