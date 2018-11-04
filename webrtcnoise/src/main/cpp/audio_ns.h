#ifndef _AUDIO_NS_H_
#define _AUDIO_NS_H_

int audio_ns_init(int sample_rate);


int audio_ns_process(int ns_handle ,  short *src_audio_data ,short *dest_audio_data);


void audio_ns_destroy(int ns_handle);

#endif