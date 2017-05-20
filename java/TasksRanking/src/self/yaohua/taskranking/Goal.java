package self.yaohua.taskranking;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class Goal {
	public String description;
	public Double revenue;
	
	public Goal(Double revenue, String description) {
		this.description = description;
		this.revenue = revenue;
	}
	
	// Ids of all goals.
	
	static final Map<String, Goal> goals = ImmutableMap.<String, Goal>builder()

		.build();
}
