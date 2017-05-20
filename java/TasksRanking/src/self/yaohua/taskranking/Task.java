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
	
	// Description of all tasks.
	static final Map<String, Task> tasks = ImmutableMap.<String, Task>builder()
			
		.build();
}
