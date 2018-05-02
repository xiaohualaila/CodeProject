package hardware;

/**
 * Created by xujin on 2017/2/14.
 */

public class Hardware {
    static{
        System.loadLibrary("Hardware");
    }
    public native String getJniString();
    /*openWiegand 打开韦根门禁*/
    public native int openWiegand();
    /*readWiegand 读取门禁卡号*/
    public native int readWiegand();
    /*closeWiegand 关闭韦根门禁*/
    public native int closeWiegand();
    /*openWatchdog 打开看门狗*/
    public native int openWatchdog();
    /*
    ioctlWatchdog 控制看门狗
    cmd = 3 喂狗
    cmd = 4 设置超时时间 arg 超时时间
    */
    public native int ioctlWatchdog(int cmd,int arg);
    /*closeWatchdog 关闭看门狗*/
    public native int closeWatchdog();
    /*
    openSerialPort  打开串口
    devName         串口号
    baud            波特率
    dataBit         数据位
    stopBits        停止位
     */
    public native int openSerialPort(String devName, long baud, int dataBit, int stopBits);
    /*
    writeSerialPort 写入数据到串口
    fd              文件描述符
    data            要写入的数据
    */
    public native int writeSerialPort(int fd,byte[] data);
    /*
    readSerialPort  从串口读取数据
    fd              文件描述符
    buf             读取数据缓存
    len             读取的数据长度
     */
    public native int readSerialPort(int fd,byte[] buf,int len);
    /*
    select          监视文件描述符
    fd              文件描述符
    sec             等待时间（秒）seconds
    usec            等待时间（微秒）microseconds
     */
    public native int select(int fd,int sec,int usec);
    /*
    closeSerialPort 关闭串口
    fd              文件描述符
     */
    public native int closeSerialPort(int fd);
}
