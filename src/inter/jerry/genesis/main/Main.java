package inter.jerry.genesis.main;

import java.time.LocalTime;

public class Main {
	public static void main(String[] args) {
		
		int companyNum = 20;
		int taskNum = 10;
		try {
			if(args.length==2 && Integer.valueOf(args[0])>2 && Integer.valueOf(args[1])>0) {
				companyNum = Integer.valueOf(args[0]);
				taskNum = Integer.valueOf(args[1]);
			}else {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("Parameter error, use default value");
		}
		
		System.out.printf("[%s] Created Company with %d people (include LT&PM)\n", LocalTime.now(), companyNum);
		System.out.printf("[%s] Created Task-1 ~ Task-%d\n", LocalTime.now(), taskNum);

		Company company = new Company(companyNum);

		for (int i = 1; i <= taskNum; i++) {
			Task task = new Task("Task-" + i);
			company.execute(task);
		}
	}
}
