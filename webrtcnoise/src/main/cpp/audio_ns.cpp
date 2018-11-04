#include "audio_ns.h"
#include "noise_suppression.h"

#include <stdio.h>

int audio_ns_init(int sample_rate){

	NsHandle* NS_instance;
	int ret;
	if ((ret = WebRtcNs_Create(&NS_instance) )) {
		printf("WebRtcNs_Create failed with error code = %d", ret);
		return ret;
	}
	if ((ret = WebRtcNs_Init(NS_instance, sample_rate) )) {
		printf("WebRtcNs_Init failed with error code = %d", ret);
		return ret;
	}

	if ( ( ret =  WebRtcNs_set_policy(NS_instance, 2) ) ){
		printf("WebRtcNs_set_policy failed with error code = %d", ret);
		return ret;
	}

	return (int)NS_instance;
}


int audio_ns_process(int ns_handle ,  short *src_audio_data ,short *dest_audio_data){
	//get handle
	NsHandle* NS_instance = (NsHandle* )ns_handle;

	//noise suppression
	if(
		WebRtcNs_Process(NS_instance ,src_audio_data ,NULL ,dest_audio_data , NULL) ||
		WebRtcNs_Process(NS_instance ,&src_audio_data[80] ,NULL ,&dest_audio_data[80] , NULL) ){
			printf("WebRtcNs_Process failed with error code = " );
			return -1;
	}

	return 0;
}


void audio_ns_destroy(int ns_handle){
	WebRtcNs_Free((NsHandle *) ns_handle);
}