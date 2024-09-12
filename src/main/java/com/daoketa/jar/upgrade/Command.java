package com.daoketa.jar.upgrade;

import lombok.AllArgsConstructor;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wangcy 2024/9/12 8:45
 */
@AllArgsConstructor
public class Command {
	
	public enum Tag {
		s("-s"), p("-p"), all("--all"),
		;
		String value;
		Tag(String value) {
			this.value = value;
		}
		boolean is(String str) {
			return value.equals(str);
		}
		static boolean isAll(String str) {
			return all.value.equals(str);
		}
		static boolean isNormal(String str) {
			return Arrays.stream(values()).filter(tag -> tag != all).anyMatch(tag -> tag.value.equals(str));
		}
	}
	
	static List<Command> extract(String[] args) {
		Map<String, Command> commandMap = new LinkedHashMap<>();
		if(args != null && args.length != 0) {
			List<String> argList = Arrays.stream(args)
					.filter(x -> !Tag.isAll(x))
					.collect(Collectors.toList());
			boolean all = argList.size() != args.length;
			if(all) {
				commandMap.put(Tag.all.value, new Command(Tag.all.value, null));
			}
			for(int i=0,len=argList.size(); i<len; i++) {
				String tag = argList.get(i);
				Assert.isTrue(Tag.isNormal(tag), "不识别的参数 " + tag);
				i++;
				Assert.isTrue(i < len, "参数值丢失 " + tag);
				Assert.isTrue(!commandMap.containsKey(tag), "重复的参数 " + tag);
				commandMap.put(tag, new Command(tag, argList.get(i)));
			}
		}
		boolean conflict = commandMap.containsKey(Tag.p.value) && commandMap.containsKey(Tag.all.value);
		Assert.isTrue(!conflict, "参数冲突 " + Tag.p.value + "," + Tag.all.value);
		return new ArrayList<>(commandMap.values());
	}

	final String tag;
	final String param;
	
}
