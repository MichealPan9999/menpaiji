package cn.ktc.jkf.utils;

import java.nio.ByteBuffer;

public class Buffers {
    /**
     * byte[]和ByteBuffer各自的个数
     */
    private static final int CAPACITY = 200;
    private static final int BUFFER_SIZE = 200*1024;

    private final Object syncDataBuffer = new Object();
    private DataBufferInfo dataBufferList = null;
    
    private final Object syncByteBuffer = new Object();
    private ByteBufferInfo byteBufferList = null;
    
    public Buffers () {
        int capacity = CAPACITY;
        while(capacity-- > 0) {
            DataBufferInfo dataBufferInfo = new DataBufferInfo(BUFFER_SIZE);
            dataBufferInfo.next = dataBufferList;
            dataBufferList = dataBufferInfo;
            
            ByteBufferInfo byteBufferInfo = new ByteBufferInfo(BUFFER_SIZE);
            byteBufferInfo.next = byteBufferList;
            byteBufferList = byteBufferInfo;
        }
    }
    
    public DataBufferInfo getBytes(ByteBuffer byteBuffer) {   
        int length = byteBuffer.limit() - byteBuffer.position();
        DataBufferInfo bufferInfo = getDataBuffer(length);
        byteBuffer.get(bufferInfo.data, 0, length);
        bufferInfo.length = length;
        return bufferInfo;
    }
        
    private DataBufferInfo getDataBuffer(int size) {
        if (size > BUFFER_SIZE) {
            return new DataBufferInfo(size);
        }
        synchronized (syncDataBuffer) {
            if (dataBufferList != null) {
                DataBufferInfo buffer = dataBufferList;
                dataBufferList = dataBufferList.next;
                buffer.next = buffer;
                return buffer;
            }
        }
        return new DataBufferInfo(size);
    }

    public ByteBufferInfo  getByteBuffer(byte[] data, int offset, int len) {
        ByteBufferInfo bufferInfo = getByteBufferInfo(len);
        bufferInfo.byteBuffer.clear();
        bufferInfo.byteBuffer.put(data, offset, len);
        bufferInfo.byteBuffer.flip();
        return bufferInfo;
    }
    
    private ByteBufferInfo getByteBufferInfo(int size) {
        if(size > BUFFER_SIZE) {
            return new ByteBufferInfo(size);
        }
        synchronized (syncByteBuffer) {
            if(byteBufferList != null) {
                ByteBufferInfo buffer = byteBufferList;
                byteBufferList = byteBufferList.next;
                buffer.next = buffer;
                return buffer;
            }
        }
        return new ByteBufferInfo(size);
    }
    
    public class DataBufferInfo {
        private byte[] data;
        private int length;
        DataBufferInfo next;
        DataBufferInfo(int length) {
            data = new byte[length];
        }
        //不再判断缓冲区长度
        void put(ByteBuffer byteBuffer) {
            this.length = byteBuffer.limit();
            byteBuffer.get(data, 0, this.length);
        }
        
        public byte[] getBytes() {
            return this.data;
        }
        
        public int length() {
            return this.length;
        }

        public void recycle() {
            if (this.next == null) {
                return;
            }
            synchronized (syncDataBuffer) {
                this.next = dataBufferList;
                dataBufferList = this;
            }
        }
        public byte[] array() {
            byte[] ds = new byte[this.length];
            System.arraycopy(this.data, 0, ds, 0, this.length);
            return ds;
        }
        
        public ByteBuffer wrapByteBuffer() {
            ByteBuffer byteBuffer = ByteBuffer.wrap(data, 0, length);
            return byteBuffer;
        }
    }
    
    public class ByteBufferInfo {
        ByteBuffer byteBuffer;
        ByteBufferInfo next;
        
        ByteBufferInfo(int length) {
            byteBuffer = ByteBuffer.allocate(length);
        }
        
        public ByteBuffer getByteBuffer() {
            return byteBuffer;
        }
        
        public void recycle() {
            if (this.next == null) {
                return;
            }
            synchronized (syncByteBuffer) {
                this.next = byteBufferList;
                byteBufferList = this;
            }
        }
    }
}
