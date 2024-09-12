package com.daoketa.jar.upgrade;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;

/**
 * @author wangcy 2024/9/11 16:58
 */
public class Upgrade {
	
	private final File dir = new File(System.getProperty("user.dir"));
	private String source = "goisan-admin.jar";
	private List<File> patchList = new ArrayList<>();
	
	public Upgrade(String[] args) {
		List<Command> commandList = Command.extract(args);
		commandList.stream().filter(c -> Command.Tag.s.is(c.tag)).findFirst().ifPresent(c -> source = c.param);
		commandList.stream().filter(c -> Command.Tag.isAll(c.tag)).findFirst().ifPresent(c -> {
			File[] files = dir.listFiles();
			if(files != null) {
				Arrays.stream(files).filter(File::exists).filter(file -> file.getName().endsWith(".jar")).forEach(file -> patchList.add(file));
			}
		});
		if(patchList.isEmpty()) {
			commandList.stream().filter(c -> Command.Tag.p.is(c.tag)).findFirst().ifPresent(c -> {
				if(c.param.contains(",")) {
					Arrays.stream(c.param.split(",")).filter(StringUtils::isNotEmpty).forEach(p -> patchList.add(new File(dir, p)));
				}else {
					patchList.add(new File(dir, c.param));
				}
			});
		}
		Assert.notEmpty(patchList, "没有找到补丁");
	}
	
	public void execute() throws Exception {
		Map<String, File> patchMap = patchList.stream().collect(Collectors.toMap(File::getName, Function.identity()));
		JarFile jarFile = new JarFile(new File(dir, source));
		JarOutputStream jos = new JarOutputStream(new FileOutputStream(new File(dir, source.replace(".jar", "-" + System.currentTimeMillis() + ".jar"))));
		Enumeration<JarEntry> entries = jarFile.entries();
		while(entries.hasMoreElements()) {
			JarEntry jarEntry = entries.nextElement();
			File file = patchMap.get(jarEntry.getName().replace("BOOT-INF/lib/", ""));
			byte[] buf = new byte[1024];
			int len;
			if(file != null) {
				System.out.println("替换补丁 " + file.getName());
				JarEntry next = new JarEntry(jarEntry.getName());
				InputStream is = new FileInputStream(file);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				CRC32 crc = new CRC32();
				while((len = is.read(buf)) != -1) {
					crc.update(buf, 0, len);
					bos.write(buf, 0, len);
				}
				next.setMethod(ZipEntry.STORED);
				next.setSize(bos.size());
				next.setCrc(crc.getValue());
				jos.putNextEntry(next);
				jos.write(bos.toByteArray());
				is.close();
			}else {
				jos.putNextEntry(jarEntry);
				InputStream is = jarFile.getInputStream(jarEntry);
				while((len = is.read(buf)) != -1) {
					jos.write(buf, 0, len);
				}
				is.close();
			}
		}
		jos.close();
	}
	
}
