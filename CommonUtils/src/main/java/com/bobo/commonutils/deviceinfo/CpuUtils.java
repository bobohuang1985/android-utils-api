package com.bobo.commonutils.deviceinfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobohuang(bobohuang1985@qq.com) on 2016/7/8.
 */
public class CpuUtils {
    /**
     * Get cpu's current frequency
     * unit:KHZ
     * 获取cpu当前频率,单位KHZ
     * @return
     */
    public static List<Integer> getCpuCurFreq() {
        List<Integer> results = new ArrayList<Integer>();
        String freq = "";
        FileReader fr = null;
        try {
            int cpuIndex = 0;
            Integer lastFreq = 0;
            while(true){
                File file = new File("/sys/devices/system/cpu/cpu"+cpuIndex+"/");
                if(!file.exists()){
                    break;
                }
                file = new File("/sys/devices/system/cpu/cpu"+cpuIndex+"/cpufreq/");
                if(!file.exists()){
                    lastFreq = 0;
                    results.add(0);
                    cpuIndex++;
                    continue;
                }
                file = new File("/sys/devices/system/cpu/cpu"+cpuIndex+"/cpufreq/scaling_cur_freq");
                if(!file.exists()){
                    results.add(lastFreq);
                    cpuIndex++;
                    continue;
                }
                fr = new FileReader(
                        "/sys/devices/system/cpu/cpu"+cpuIndex+"/cpufreq/scaling_cur_freq");
                BufferedReader br = new BufferedReader(fr);
                String text = br.readLine();
                freq = text.trim();
                lastFreq = Integer.valueOf(freq);
                results.add(lastFreq);
                fr.close();
                cpuIndex++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(fr!=null){
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return results;
    }
}
