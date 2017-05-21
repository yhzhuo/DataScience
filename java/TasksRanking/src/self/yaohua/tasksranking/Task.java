package self.yaohua.taskranking;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class Task {
	public double cost;
	public String description;
	
	// Goal, impact
	public Map<String, Double> impactsToGoals;
	
	public Task(double cost, Map<String, Double> impact, String description) {
		this.cost = cost;
		this.impactsToGoals = impact;
		this.description = description;
	}
	
	// Ids of all tasks.
	
	static final String ensureQualityAndSpeedOfEachTask = "ensureQualityAndSpeedOfEachTask";
	static final String talkMoreWithTeamMembersAboutTheirTasks = "talkMoreWithTeamMembersAboutTheirTasks";
	static final String dateGirls = "dateGirls";
	static final String learnCooking = "learnCooking";
	static final String attendSocialActivities = "attendSocialActivities";
	static final String alwaysReadSocialInformation = "alwaysReadSocialInformation";
	
	// Description of all tasks.
	static final Map<String, Task> tasks = ImmutableMap.<String, Task>builder()
			.put(ensureQualityAndSpeedOfEachTask, new Task(3.0, ImmutableMap.<String, Double>builder()
					.put(Goal.haveGoodCareer, 0.95)
					.put(Goal.formStableAndSolidSocialGroups, 0.2)
				.build(),
				"Ensure the quality, quantity, and speed of each tasks you finish, and never rush."))
			
			.put(talkMoreWithTeamMembersAboutTheirTasks, new Task(1.75, ImmutableMap.<String, Double>builder()
					.put(Goal.haveGoodCareer, 0.5)
					.put(Goal.formStableAndSolidSocialGroups, 0.75)
				.build(),
				"Talk with others about there tasks, especially the ones collaborate closely with you. Know scope of each tasks and each ones tasks, know each one's responsibility, communicate first before working on other's responsibilities."))
			
			.put(dateGirls, new Task(2.75, ImmutableMap.<String, Double>builder()
					.put(Goal.gettingMarried, 0.95)
					.put(Goal.formStableAndSolidSocialGroups, 0.55)
				.build(),
				"Date girls with the goal of getting married. Try to learn from them and know their ideas."))
			
			.put(learnCooking, new Task(2.0, ImmutableMap.<String, Double>builder()
					.put(Goal.gettingMarried, 0.55)
					.put(Goal.formStableAndSolidSocialGroups, 0.2)
				.build(),
				"Learn to cook good, nice, and delicious food."))
			
			.put(attendSocialActivities, new Task(2.75, ImmutableMap.<String, Double>builder()
					.put(Goal.gettingMarried, 0.45)
					.put(Goal.formStableAndSolidSocialGroups, 0.95)
				.build(),
				"Attend off line social activities received from various sources."))
				
			.put(alwaysReadSocialInformation, new Task(2.75, ImmutableMap.<String, Double>builder()
					.put(Goal.gettingMarried, 0.15)
					.put(Goal.formStableAndSolidSocialGroups, 0.55)
				.build(),
				"Form the habit of always read social related information from various channels, and extract valuable information relate to social activities."))
		.build();
}
