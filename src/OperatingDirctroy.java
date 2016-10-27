import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.Test;

/**
 * 熟悉如何处理目录和目录树；
 */
public class OperatingDirctroy {

	/**
	 * 在指定的目录下中查找指定类型的文件(不含子目录)
	 * @throws IOException
	 */
	@Test
	public void findFile() {
		// 设定起始路径
		Path dir = Paths.get("D://life");
		// 写在try()中stream会自动调flush()，close()，所我们就不用写了；
		// 声明过滤流
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.mp4")) {
			// 输出所有Mp4文件；
			for (Path entry : stream) {
				System.out.println(entry.getFileName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 遍历目录树，搜寻目录树中的文件，（含子目录）；
	 */
	@Test
	public void traverseFile() {
		// 遍历的目录
		Path startingDir = Paths.get("E:/文档");

		// jdk7支持整个目录树的遍历，其中Files.walkFileTree()是关键方法，
		// 它的参数分别表示是遍历的目录和遍历规则，其中遍历规则要继承SimpleFileVisitor类；
		try {
			Files.walkFileTree(startingDir, new SimpleFileVisitor<Path>() {
				// 重写该访问文件的方法
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					// 找到你要找到的文件后缀
					if (file.toString().endsWith(".txt")) {
						System.out.println(file.getFileName());
					}
					return FileVisitResult.CONTINUE;
				}

			});
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
