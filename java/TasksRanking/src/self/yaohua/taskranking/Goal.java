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
	
	// 3.2
	static final String haveGoodCareer = "haveGoodCareer";
	static final String gettingMarried = "gettingMarried";
	static final String formStableAndSolidSocialGroups = "formStableAndSolidSocialGroups";
	
	static final Map<String, Goal> goals = ImmutableMap.<String, Goal>builder()
		.put(haveGoodCareer, new Goal(10.0, "As a new employee I plan to stay at my company for a while, working on interesting projects, and getting promoted."))
		.put(gettingMarried, new Goal(9.0, "Getting married with a good girl and prepare to be a good husband and father in future."))
		.put(formStableAndSolidSocialGroups, new Goal(5.0, "Meeting with different people, know there thoughts, and form stable and solid social groups."))
	.build();
}
