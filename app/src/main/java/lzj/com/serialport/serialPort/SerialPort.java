/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package lzj.com.serialport.serialPort;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class SerialPort {

	private static final String TAG = "SerialPort";

	/*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;

	public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {

		//如果串口权限不够，改变权限
		 Log.d(TAG, "device.canRead(): "+device.canRead());
		Log.d(TAG, "device.canWrite(): "+device.canRead());
		Log.d(TAG, "device.canRead(): "+device.canRead());
		Log.d(TAG, "ddevice): "+device);
		Log.d(TAG, "baudrate: "+baudrate);
		if (!device.canRead() || !device.canWrite()) {
			try {
				/* Missing read/write permission, trying to chmod the file */
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
						+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.i(TAG, "SerialPort: ==="+e.getMessage());
				throw new SecurityException();
			}
		}

		mFd = open(device.getAbsolutePath(), baudrate, flags);
		if (mFd == null) {
			Log.e(TAG, "native open returns null");
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);

		if (mFileInputStream==null){
			Log.d("mFileInputStream", "SerialPort: ==null");
		}else {
			Log.d("mFileInputStream", "SerialPort: 不为空");
		}
//		try {
//			byte[] bytes = new byte[1024];
//			while (mFileInputStream.read(bytes) != -1) {
//				Log.d("mFileInputStream", "---------------->" + new String(bytes));
//			}
//		}catch (Exception e){
//			Log.d("mFileInputStream", "----88888---->" );
//		}
		mFileOutputStream = new FileOutputStream(mFd);

	}

	// Getters and setters
	public InputStream getInputStream() {
		return mFileInputStream;
	}

	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}


	// JNI
	private native static FileDescriptor open(String path, int baudrate, int flags);
	public native void close();
	static {
		System.loadLibrary("serial_port");
	}

	public void exeShell(String cmd){

		try{
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							p.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				Log.i("exeShell",line);
			}

		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
}
