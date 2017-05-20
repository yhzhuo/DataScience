package self.yaohua.taskranking;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class Goal {
	public String description;
	public Double value;
	
	public Goal(Double value, String description) {
		this.description = description;
		this.value = value;
	}
	
	// Ids of all goals.
	
	static final Map<String, Goal> goals = ImmutableMap.<String, Goal>builder()

		.build();
}
