import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

/*
 * 目的：熟悉Path类中的常用方法；
 */
public class OperatingPath {

	@Test
	public void Test() {
		// 得到文件路径,有两种方法；
		Path path1 = Paths.get("E:/Programe/./HelloJava.java");
		Path path2 = FileSystems.getDefault().getPath("E:/floder1/floder2//floder3//1.txt");
		// 获取该路径下的当前文件名称；
		System.out.println("文件名称:" + path1.getFileName());
		// 获取当前路径下的文件的所在层数（即“//”数）；
		System.out.println("获取文件层数" + path1.getNameCount());
		//获取上一层路径
		System.out.println("上层路径：" + path1.getParent());
		//获取该路径所在盘符
		System.out.println("路径的根节点：" + path1.getRoot());
		//得到该路径的一个子路径(但)，可从0开始，不包括最右边界；
		System.out.println(path1.subpath(0,2));
		
		//去掉路径中冗余信息
		Path normalizedPath=path1.normalize();//去掉了冗余符号"./"，留下了有用的文件名称
		System.out.println("去掉冗余符号后的路径：" + normalizedPath);
		
		/*
		 * toRealPath()方法:融合了toAbsolutePath()和normalize()两个方法的功能
		 * 作用：将一个文件的相对路径变为绝对路径；
		 * 注:相对路径是相对于当前项目路径而言，即：在相对的路径前加上当前项目的路径；
		 */
		try {
			Path partPath = Paths.get("src/说明.txt");
			Path realPath = partPath.toRealPath();		
			System.out.println("realPath：" + realPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//将两个路径合并成一个新路径,注意一个路径前不能有“/”,否则不能合并成功；
		Path prefix=Paths.get("/第一个路径");
		Path completePath=prefix.resolve("第二个路径");
		System.out.println(prefix.resolve(completePath));
		
	}
}
