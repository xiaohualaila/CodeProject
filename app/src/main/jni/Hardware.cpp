//
// Created by xujin on 2016/12/23.
//
#include <jni.h>
#include "hardware_Hardware.h"
#include <fcntl.h> /*包括文件操作，如open() read() close() write()等*/
#include <linux/rtc.h>
#include <stdio.h>
#include <time.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/ioctl.h>
#include <errno.h>
#include <string.h>
#include <termios.h>


int fd_wiegand = -1;
int fd_watchdog = -1;
/*
 * Class:     io_github_yanbober_ndkapplication_NdkJniUtils
 * Method:    getCLanguageString
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_hardware_Hardware_getJniString(JNIEnv *env, jobject obj)
{
	//return (*env)->NewStringUTF(env,"This just a test for Android Studio NDK JNI developer!");
	return env->NewStringUTF("Hello from JNI !");
}
  
JNIEXPORT jint JNICALL Java_hardware_Hardware_openWiegand(JNIEnv *, jobject)
{
	if(fd_wiegand != -1)
	    close(fd_wiegand);
	fd_wiegand = open("/dev/wiegand", O_RDONLY);
	return fd_wiegand;
}
 
JNIEXPORT jint JNICALL Java_hardware_Hardware_readWiegand(JNIEnv *, jobject)
{
	int arg = 0;
	return ioctl(fd_wiegand,26, arg);
}

JNIEXPORT jint JNICALL Java_hardware_Hardware_closeWiegand(JNIEnv *, jobject)
{
	if(fd_wiegand != -1)
	    close(fd_wiegand);
	return 1;
}

JNIEXPORT jint JNICALL Java_hardware_Hardware_openWatchdog(JNIEnv *, jobject)
{
    if(fd_watchdog != -1)
    	close(fd_watchdog);
    fd_watchdog = open("/dev/watchdog", O_RDONLY);
    return fd_watchdog;
}

JNIEXPORT jint JNICALL Java_hardware_Hardware_ioctlWatchdog(JNIEnv *, jobject, jint cmd, jint arg)
{
    return ioctl(fd_watchdog,cmd,&arg);
}

JNIEXPORT jint JNICALL Java_hardware_Hardware_closeWatchdog(JNIEnv *, jobject)
{
    if(fd_watchdog != -1)
    	close(fd_watchdog);
    return 1;
}

JNIEXPORT jint JNICALL Java_hardware_Hardware_openSerialPort
( JNIEnv* env,jobject thiz , jstring devName, jlong baud, jint dataBits, jint stopBits)
{
    int fd = -1;
    const char *str = env->GetStringUTFChars(devName,NULL);
    fd = open(str, O_RDWR);
    env->ReleaseStringUTFChars(devName, str);
    if(fd == -1)
    return fd;

    int speed_arr[] = { B9600, B19200, B38400, B57600, B115200, B115200, B115200 ,B1152000};
    struct termios attr;
    int speed_num;

    tcgetattr(fd, &attr);
    speed_num = (baud/19200) > 6 ? 7 : (baud/19200);
    cfsetispeed(&attr, speed_arr[speed_num]);
    cfsetospeed(&attr, speed_arr[speed_num]);

    attr.c_iflag = 0;
    attr.c_oflag = 0;
    attr.c_lflag = 0;

    attr.c_cc[VTIME] = 0;
    attr.c_cc[VMIN] = 0;
    attr.c_cflag |= CLOCAL|CREAD;
    attr.c_cflag &= ~CSIZE;
    attr.c_cflag |= CS8;
    attr.c_cflag &= ~PARENB;  //无奇偶校验位
    attr.c_cflag &= ~CSTOPB;

    tcsetattr(fd, TCSANOW, &attr);

    return fd;
}

JNIEXPORT jint JNICALL Java_hardware_Hardware_writeSerialPort
( JNIEnv* env,jobject thiz ,jint fd, jbyteArray data)
{
	jbyte * str = env->GetByteArrayElements(data,NULL);
	jint length = env->GetArrayLength(data);
	int num = write(fd,str,length);
	env->ReleaseByteArrayElements(data, str, 0);
	return num;
}

JNIEXPORT jint JNICALL Java_hardware_Hardware_readSerialPort
( JNIEnv* env,jobject thiz ,jint fd, jbyteArray buf, jint len )
{
	jbyte * str = env->GetByteArrayElements(buf,NULL);
	int num = read(fd,str,len);
	env->ReleaseByteArrayElements(buf, str, 0);
	return num;
}

JNIEXPORT jint JNICALL Java_hardware_Hardware_select
( JNIEnv* env,jobject thiz ,jint fd, jint sec,jint usec)
{
    fd_set fds;
    struct timeval timeout={sec,usec};
    FD_ZERO(&fds);
    FD_SET(fd, &fds);
    return select(fd+1,&fds,NULL,NULL,&timeout);
}

JNIEXPORT jint JNICALL Java_hardware_Hardware_closeSerialPort
( JNIEnv* env,jobject thiz ,jint fd)
{
	return close(fd);
}