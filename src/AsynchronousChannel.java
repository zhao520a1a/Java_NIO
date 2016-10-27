import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;

/**
 * 熟悉NIO2异步读写操作： jdk7提供了三个新的异步通道： 用于文件的IO：AsynchronousFileChannel类
 * 用于套接字IO(支持超时):AsynchronousSocketChannel类
 * 用于套接字IO(接受异步连接)：AsynchronousServerSocketChannel类 使用时，主要有两种形式：将来式和回调式 将来式：
 * 要使用java.util.concurrent.Future接口 适用场合:希望由主控线程发起IO操作，并轮询等到有结果时； 回调式：
 * 主线程会派一个侦查员CompletionHandler要独立线程中执行IO操作，
 * 它将带着IO操作的结果返回到主线程中，而结果会触发它自己的completed或failed方法（需自己重写）；
 * 适用场景：在异步事件刚一成功或失败需要马上采取行动时；
 * 
 */
public class AsynchronousChannel {

	@Test
	// 将来式：
	public void futureDemo() {
		Path path = Paths.get("E://听力10小时特训//十小时听力 第8课 有广告.avi");
		try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(path);) {
			ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024); // 设缓存
			Future<Integer> result = channel.read(buffer, 0); // 得到返回结果

			while (!result.isDone()) {
				// 在得到IO结果前，主线程期间可以做其他的事情；
				System.out.println("主线程在工作中……");
			}
			Integer bytesRead = result.get();
			System.out.println("read:" + bytesRead);//输出的是最后一次缓冲池中的容量；

		} catch (Exception e) {
		}
	}

	@Test
	// 回调式：
	public void callbackDemo() {
		Path path = Paths.get("E://听力10小时特训//十小时听力 第8课 有广告.avi");
		AsynchronousFileChannel channel = null;
		try {
			channel = AsynchronousFileChannel.open(path);
			ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
			channel.read(buffer, 0, null, new CompletionHandler() {
				@Override
				public void completed(Object result, Object attachment) {
					System.out.println("Success！");
					System.out.println("Bytes Read = " + result); //输出的是最后一次缓冲池中的容量；
				}

				@Override
				public void failed(Throwable exc, Object attachment) {
					System.out.println("Fail！");
					System.out.println(exc.getCause());
				}
			});

			System.out.println("主线程在工作中……");
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (channel != null) {
					channel.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
