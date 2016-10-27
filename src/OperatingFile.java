
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * 熟悉NIO2中文件系统的操作： 主要类： java.nio.file.Files类；
 * 
 * @author 小鑫哦
 *
 */
public class OperatingFile {

	@Test
	public void test() {
		Path path1 = Paths.get("E://1.txt");
		// createFile(path1);
		// deleteFile(path1);
		// Path path2 = Paths.get("E://2.txt");
		// createFile(path2);
		// this.copyFile(path1, path2);
		// this.setDosAttr(path2);
		// this.getDosAttr(path2);
		// this.writerFile(path1);
		// this.readFile(path1);
		// this.simpleReadFile(path1);
		// new TestFileSystem().monitorFile(path1);
		// this.readData(path1);
		// this.writeData(path1);
	}

	// 创建文件,Files.creatFile(path);
	public void createFile(Path path) {
		try {
			Files.createFile(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 删除文件,Files.delete(path);
	public void deleteFile(Path path) {
		try {
			Files.delete(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 移动文件,Files.move(source, path, options)
	public void moveFile(Path source, Path target) {
		try {
			// 其中REPLACE_EXISTING表示若有文件存在可以替换，ATOMIC_MOVE表示是原子操作（保证了双方操作都要成功）；
			Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 拷贝文件 Files.copy(source, path, options)
	public void copyFile(Path source, Path path) {
		try {
			// 其中REPLACE_EXISTING表示若有相同文件存在可以替换，COPY_ATTRIBUTES表示文件属性也复制；
			Files.copy(source, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 在DOS系统中，设置已经存在的文件属性；
	public void setDosAttr(Path path) {
		// 得到文件的属性视图
		DosFileAttributeView view = Files.getFileAttributeView(path, DosFileAttributeView.class);
		try {
			view.setReadOnly(true); // 设置文件只读属性
			// view.setHidden(true); //设置文件隐藏属性
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 在DOS系统中,获得已存在文件属性的信息；
	public void getDosAttr(Path path) {
		// 得到文件的属性视图
		DosFileAttributeView view = Files.getFileAttributeView(path, DosFileAttributeView.class);
		if (view != null) {
			try {
				// 得到文件的所有属性
				DosFileAttributes attrs = view.readAttributes(); // 得到文件的所有属性
				System.out.println("最后修改时间:" + attrs.lastAccessTime());
				System.out.println("文件是否是只读的：" + attrs.isReadOnly());
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	// 在Posix（可移植操作系统接口）文件系统中，设置已经存在的文件属性；例如：linux，unix系统；
	public void setPosixAttr(Path path) {
		try {
			// 得到所有文件属性
			PosixFileAttributes attrs = Files.readAttributes(path, PosixFileAttributes.class);
			// 得到文件的访问许可
			Set<PosixFilePermission> permissions = attrs.permissions();
			// 将访问许可清空
			permissions.clear();
			// 定义自己的许可，使用permissions.add();
			permissions.add(PosixFilePermission.OWNER_WRITE);// 添加所有者写权限
			permissions.add(PosixFilePermission.OWNER_READ);// 添加所有者读权限
			permissions.add(PosixFilePermission.GROUP_READ);// 添加组用户读权限
			permissions.add(PosixFilePermission.OTHERS_READ);// 添加其他用户读权限
			Files.setPosixFilePermissions(path, permissions); // 设置文件的权限
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 用BufferedWriter写文件
	public void writerFile(Path path) {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writer.newLine();
			writer.write("有你真好！");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 用BufferedReader读文件
	public void readFile(Path path) {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 不用循环的方式读文件，Files提供了两个辅助方法用来读取文件中的全部的行和全部字节；
	public void simpleReadFile(Path path) {
		try {
			List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);// 读取文件中的全部的行
			byte[] bytes = Files.readAllBytes(path);// 读取文件中的全部字节；

			System.out.println(lines);
			System.out.println(new String(bytes, "utf-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 监听文件夹下的文件(不包含子文件)
	public void monitorFile(Path path) {
		WatchService watcher = null;
		WatchKey key = null;
		try {
			// 创建一个文件监听器
			watcher = FileSystems.getDefault().newWatchService();
			// 注册一个监视key；
			key = path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
			// 使用用户线程监视注册文件或目录的变化
			boolean start = true;
			while (start) {
				key = watcher.take(); // 得到当时key
				// 遍历key中的事件，检查是否为修改事件
				for (WatchEvent<?> event : key.pollEvents()) {
					if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
						System.out.println("文件发生了修改！");
					}
				}
				key.reset();// 注意：要重置监测key，才能继续监测；
				// key.cancel();
				// start = false;//停止监视
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// 在保持当前位置的情况 下，向文件中写入数据，默认是在文件开头写入；
	public void writeData(Path path) {
		// 创建FileChannel：两种方法：
		// FileChannel fc = new RandomAccessFile(string,options).getChannel();
		// FileChannel fc = FileChannel.open(path,options);
		try (FileChannel fc = FileChannel.open(path, StandardOpenOption.WRITE);) {
			String s = "你好啊！";
			// 向指定的位置写入数据，注意：它会以覆盖的形式写入数据；
			fc.write(ByteBuffer.wrap(s.getBytes()), fc.size() - 60);

			// 向文件末尾添加数据
			fc.position(fc.size()); // 定位到文件末尾
			fc.write(ByteBuffer.wrap(s.getBytes()));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 在保持当前位置的情况 下，向文件中读取数据；
	public void readData(Path path) {
		// 开辟缓存区
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ);) {
			// 指定位置：读取文件中的最后100个字符
			fc.read(buffer, fc.size() - 30);
			buffer.flip();// 注意：使指针返回到缓冲区的第一个位置,在读取之前调用
			System.out.println(Charset.forName("utf-8").decode(buffer));

			// 用while循环，读取文件的全部内容；
			// int n = -1;
			// while ((n = fc.read(buffer)) != -1) {
			// System.out.println(new String(buffer.array(), 0, n));
			// }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
