package com.mfma.sofaboltdemo.util;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.math.BigDecimal;

import org.apache.commons.lang.ArrayUtils;

/**
 * @program: csizg-iot
 * @description: ${description}
 * @author: csizg
 * @create: 2018-06-17 22:45
 **/
public class NettyUtils {

    /**
     * 获取十六进制表示的字符串
     *
     * @param data   源数据
     * @param offset 目标数据在源数据中的偏移量
     * @param length 目标数据的长度
     * @return
     */
    public static String getHexString(ByteBuf data, int offset, int length) {
        return ByteBufUtil.hexDump(data, offset, length);
    }


    /**
     * 获取十六进制表示的字符串
     *
     * @param data   源数据
     * @param length 目标数据的长度
     * @return
     */
    public static String getHexString(ByteBuf data, int length) {
        byte[] bytes = getBytes(data, length);
        return ByteBufUtil.hexDump(bytes);
    }

    /**
     * 获取十六进制表示的字符串
     *
     * @param data 源数据
     * @return
     */
    public static String getHexString(ByteBuf data) {
        return ByteBufUtil.hexDump(data);
    }

    /**
     * 将byte数组转化为十六进制表示的字符串
     *
     * @param data   源数据
     * @param offset 目标数据在源数据中的偏移量
     * @param length 目标数据的长度
     * @return
     */
    public static String getHexString(byte[] data, int offset, int length) {
        byte[] array = ArrayUtils.subarray(data, offset, offset + length);
        return ByteBufUtil.hexDump(array);
    }


    /**
     * 获取byte数组
     *
     * @param data   源数据
     * @param offset 目标数据在源数据中的偏移量
     * @param length 目标数据的长度
     * @return
     */
    public static byte[] getBytes(ByteBuf data, int offset, int length) {
        byte[] array = new byte[length];
        data.getBytes(offset, array);
        return array;
    }

    /**
     * 获取byte数组
     *
     * @param data   源数据
     * @param length 目标数据的长度
     * @return
     */
    public static byte[] getBytes(ByteBuf data, int length) {
        byte[] bytes = new byte[length];
        data.readBytes(bytes);
        return bytes;
    }


    /**
     * 读取一字节数据并转换为整型数据
     *
     * @param data 源数据
     * @return
     */
    public static int getInt(ByteBuf data) {
        return data.readByte() & 0xFF;
    }

    /**
     * 将一字节数据转换为整型数据
     *
     * @param data byte数据
     * @return
     */
    public static int getInt(byte data) {
        return data & 0xFF;
    }


    /**
     * 读取两字节数据并转换为整型数据
     *
     * @param data 源数据
     * @return
     */
    public static int getInt2(ByteBuf data) {
        return data.readByte() & 0xFFFF;
    }

    public static int getInt4(ByteBuf data) {
        return data.readByte() & 0xFFFFFFFF;
    }

    /**
     * 转换byte数组为int（小端）
     *
     * @return
     * @note 数组长度至少为4，按小端方式转换,即传入的bytes是小端的，按这个规律组织成int
     */
    public static int Bytes2Int_LE(byte[] bytes) {
        if (bytes.length < 4)
            return -1;
        int iRst = (bytes[0] & 0xFF);
        iRst |= (bytes[1] & 0xFF) << 8;
        iRst |= (bytes[2] & 0xFF) << 16;
        iRst |= (bytes[3] & 0xFF) << 24;
        return iRst;
    }


    /**
     * 转换byte数组为int（大端）
     *
     * @return
     * @note 数组长度至少为4，按小端方式转换，即传入的bytes是大端的，按这个规律组织成int
     */
    public static int Bytes2Int_BE(byte[] bytes) {
        if (bytes.length < 4)
            return -1;
        int iRst = (bytes[0] << 24) & 0xFF;
        iRst |= (bytes[1] << 16) & 0xFF;
        iRst |= (bytes[2] << 8) & 0xFF;
        iRst |= bytes[3] & 0xFF;

        return iRst;
    }

    /**
     * 将两字节数据转换为整型数据
     *
     * @param data byte数据
     * @return
     */
    public static int getInt2(byte data) {
        return data & 0xFFFF;
    }

    /**
     * 获取short类型数据
     *
     * @param data 源数据
     * @return
     */
    public static int getShort(ByteBuf data) {
        return data.readShort();
    }


    /**
     * 获取无符号，且使用小端表示的short类型数据
     *
     * @param data 源数据
     * @return
     */
    public static int getShortLE(ByteBuf data) {
        return data.readUnsignedShortLE();
    }

    public static int getIntLE(ByteBuf data) {
        return data.readIntLE();
    }


    /**
     * 获取小端的Long类型数据
     *
     * @param data 源数据
     * @return
     */
    public static long getLongLE(ByteBuf data) {
        return data.readLongLE();
    }


    /**
     * 获取BigDecimal类型数据
     *
     * @param data   源数据
     * @param length 目标数据长度
     * @return
     */
    public static BigDecimal getBigDecimal(ByteBuf data, int length) {
        byte[] bytes = getBytes(data, length);
        String str = new String(bytes);
        return new BigDecimal(str);
    }

    /**
     * 将经度转换为byte类型数据
     *
     * @param longitude 经度
     * @return
     */
    public static byte[] getLongitudeBytes(BigDecimal longitude) {
        String str = formatLongtitude(longitude.toString());
        String hex = ByteBufUtil.hexDump(str.getBytes());
        return ByteBufUtil.decodeHexDump(hex);
    }

    /**
     * 将维度转化为byte类型数据
     *
     * @param latitude 维度
     * @return
     */
    public static byte[] getLatitudeBytes(BigDecimal latitude) {
        String str = formatLatitude(latitude.toString());
        String hex = ByteBufUtil.hexDump(str.getBytes());
        return ByteBufUtil.decodeHexDump(hex);
    }

    /**
     * 对经度值进行格式化，转化为字符串
     *
     * @param longitude 经度
     * @return
     */
    private static String formatLongtitude(String longitude) {
        int length = longitude.length();
        length = (11 - length % 11) % 11;
        StringBuilder stringBuilder = new StringBuilder().append(longitude);
        for (int i = 0; i < length; i++) {
            stringBuilder.append("0");
        }
        return stringBuilder.toString();
    }

    /**
     * 对维度值进行格式化，转化为字符串
     *
     * @param latitude 维度
     * @return
     */
    private static String formatLatitude(String latitude) {
        int length = latitude.length();
        length = (10 - length % 10) % 10;
        StringBuilder stringBuilder = new StringBuilder().append(latitude);
        for (int i = 0; i < length; i++) {
            stringBuilder.append("0");
        }
        return stringBuilder.toString();
    }


    /**
     * 检验校验和是否正确
     *
     * @param toBeCheckedArray: 待校验数据的源数据
     * @param checkSum:    校验和
     * @return
     */
    public static boolean checkSum(byte[] toBeCheckedArray, String  checkSum) {
        byte[] bytes = getSumCheck(toBeCheckedArray);
        String data0 = ByteBufUtil.hexDump(bytes);
        return data0.equalsIgnoreCase(checkSum);
    }


    /**
     * 计算并返回校验和数据
     *
     * @param toBeCheckedArray: 待计算数据
     * @return
     */
    public static byte[] getSumCheck(byte[] toBeCheckedArray) {
        //计算header,length等参数的和
        long sum = 0;
        //逐Byte添加位数和
        for (byte checkedData : toBeCheckedArray) {
            long mNum = ((long) checkedData >= 0) ? (long) checkedData : ((long) checkedData + 256);
            sum += mNum;
        }
        //创建一个空数组用于后续存储位数和转化后的byte数据
        byte[] bytes = new byte[2];
        //位数和转化为Byte数组
        //注意按照协议校验和按照低字节在前高字节在后来组织填充到协议格式中
        for (int i = 0; i < 2; i++) {
            bytes[i] = (byte) (sum >> (i * 8) & 0xFF);
        }
        return bytes;
    }

    /**
     * 模16下的零填充
     *
     * @param data 源数据
     * @return
     */
    public static ByteBuf zeroPaddingByModulusSixteen(ByteBuf data) {
        int length = (16 - data.readableBytes() % 16) % 16;
        ByteBuf byteBuf = Unpooled.buffer(data.readableBytes() + length);
        byteBuf.writeBytes(data);
        byteBuf.writeZero(length);
        return byteBuf;
    }

    /**
     * 数字字符串转ASCII码字符串
     *
     * @param content 字符串
     * @return ASCII字符串
     */
    public static String StringToAsciiString(String content) {
        String result = "";
        int max = content.length();
        for (int i = 0; i < max; i++) {
            char c = content.charAt(i);
            String b = Integer.toHexString(c);
            result = result + b;
        }
        return result;
    }


}
