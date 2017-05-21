package self.yaohua.taskranking;

import java.util.*;
import java.util.Map.Entry;

/**
 * Ways to distinguish goals and tasks
 * 1. Simple things are tasks, complicate things are goal
 * 2. Things can finish in short run are tasks, things need to finish in long run are goals.
 * 3. Things relate to other items are tasks, things unrelated to other items are goals.
 * @author yaohua
 *
 */
public class TasksRanking {
	public static void main(String[] args) {
//		reverseSortGoalsByRevenue();
//		sortTasksByCost();
//		listGoalsImpactedByTasks();
		tasksRanking();
	}
	
	private static void tasksRanking() {
		o("\n======================================= tasksRanking =======================================");
		
		// Goal, Task, Impact.
		Map<String, Map<String, Double>> tasksImpactsOnGoals = tasksImpactsOnGoals();
		
		// TODO(yaohuazhuo): Fix the bug when cost is negative.
		// Calculate task and it's revenue.
		// task, revenue.
		Map<String, Double> tasksRevenue = new HashMap<String, Double>();
		for (Map.Entry<String, Goal> goalElement : Goal.goals.entrySet()) {
			Map<String, Double> impactingTasks = tasksImpactsOnGoals.get(goalElement.getKey());
			for (Map.Entry<String, Double> taskElement : impactingTasks.entrySet()) {
				Double originalRevenue = tasksRevenue.get(taskElement.getKey());
				Double currentRevenue = goalElement
						.getValue()
						.revenue * 
						tasksImpactsOnGoals
						.get(goalElement
								.getKey())
						.get(taskElement
								.getKey());
				tasksRevenue.put(taskElement.getKey(), originalRevenue == null ? currentRevenue : originalRevenue + currentRevenue);
			}
		}
		for (Map.Entry<String, Double> e : tasksRevenue.entrySet()) {
			e.setValue(e.getValue() - Task.tasks.get(e.getKey()).cost);
		}
		
		// Ranking.
		List<Map.Entry<String, Double>> results = new LinkedList<>(tasksRevenue.entrySet());
		Collections.sort(results, new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> arg0, Entry<String, Double> arg1) {
				return arg1.getValue() > arg0.getValue() ? 1 : -1;
			}
		});

		// Output.
		
		// Task id, revenue
		for (Map.Entry<String, Double> e : results) {
			output(0, "task: " + e.getKey() + "; revenue: " + e.getValue() + "; cost:" + Task.tasks.get(e.getKey()).cost + "; description: " + Task.tasks.get(e.getKey()).description);
			
			// Print all impacted goals and impacted portions.
			
			
			
			Set<String> impactedGoals = Task.tasks.get(e.getKey()).impactsToGoals.keySet();
			for (String impactedGoal : impactedGoals) {
				double impactPortion = tasksImpactsOnGoals.get(impactedGoal).get(e.getKey());
				output(1, "impact '" + impactedGoal + ":" + Goal.goals.get(impactedGoal).revenue + "' for "+ impactPortion);
			}
			
			
			
		}
	}
	
	private static void reverseSortGoalsByRevenue() {
		o("\n======================================= reverseSortGoalsByRevenue =======================================");
		
		List<Entry<String, Goal>> goals = new LinkedList<>(Goal.goals.entrySet());
		Collections.sort(goals, new Comparator<Entry<String, Goal>>() {
			@Override
			public int compare(Entry<String, Goal> arg0, Entry<String, Goal> arg1) {
				return arg1.getValue().revenue > arg0.getValue().revenue ? 1 : -1;
			}
		});
		for (Entry<String, Goal> e : goals) {
			output(0, e.getKey() + " : " + e.getValue().revenue);
			output(1, e.getValue().description);
			o("");
		}
	}
	
	private static void sortTasksByCost() {
		o("\n======================================= sortTasksByCost =======================================");
		
		List<Entry<String, Task>> tasks = new LinkedList<>(Task.tasks.entrySet());
		Collections.sort(tasks, new Comparator<Entry<String, Task>>() {
			@Override
			public int compare(Entry<String, Task> arg0, Entry<String, Task> arg1) {
				// TODO Auto-generated method stub
				return arg1.getValue().cost > arg0.getValue().cost ? -1 : 1;
			}
		});
		for (Entry<String, Task> e : tasks) {
			output(0, e.getKey() + " : " + e.getValue().cost);
			output(1, e.getValue().description);
			o("");
		}
	}
	
	private static void listGoalsImpactedByTasks() {
		o("\n======================================= listGoalsImpactedByTasks =======================================");
		
		// Goal, Task, Impact.
		Map<String, Map<String, Double>> tasksImpactsOnGoals = tasksImpactsOnGoals();
		List<Entry<String, Map<String, Double>>> sortedTasksImpactsOnGoals = new LinkedList<>(tasksImpactsOnGoals.entrySet());
		Collections.sort(sortedTasksImpactsOnGoals, new Comparator<Entry<String, Map<String, Double>>>() {
			@Override
			public int compare(Entry<String, Map<String, Double>> o1, Entry<String, Map<String, Double>> o2) {
				return Goal.goals.get(o1.getKey()).revenue > Goal.goals.get(o2.getKey()).revenue ? -1 : 1;
			}
		});
		Comparator<Entry<String, Double>> tasksImpactComparator = new Comparator<Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> arg0, Entry<String, Double> arg1) {
				return arg0.getValue() > arg1.getValue() ? -1 : 1;
			}
		};
		for (Entry<String, Map<String, Double>> e : sortedTasksImpactsOnGoals) {
			Goal goal = Goal.goals.get(e.getKey());
			output(0, "goal: " + e.getKey() +"; revenue: " + goal.revenue + "; description: " + goal.description);
			List<Entry<String, Double>> impactingTasks = new LinkedList<>(e.getValue().entrySet());
			Collections.sort(impactingTasks, tasksImpactComparator);
			for (Entry<String, Double> e2 : impactingTasks) {
				output(1, "impacted by: " + e2.getKey() + " with cost: " + Task.tasks.get(e2.getKey()).cost + " for: " + e2.getValue());
			}
		}
	}
	
	// Goal, Task, Impact.
	private static Map<String, Map<String, Double>> tasksImpactsOnGoals() {
		return tasksImpactsOnGoals(true /* normalize */);
	}
	
	// Tasks Ranking results will be incorrect without normalization.
	private static Map<String, Map<String, Double>> tasksImpactsOnGoals(boolean normalize) {
		Map<String, Map<String, Double>> ret = new HashMap<String, Map<String, Double>>();
		for (Map.Entry<String, Goal> goalElement : Goal.goals.entrySet()) {
			Map<String, Double> impacts = new HashMap<String, Double>();
			ret.put(goalElement.getKey(), impacts);
			for (Map.Entry<String, Task> taskElement : Task.tasks.entrySet()) {
				if (taskElement.getValue().impactsToGoals.containsKey(goalElement.getKey())) {
					impacts.put(taskElement.getKey(), taskElement.getValue().impactsToGoals.get(goalElement.getKey()));
				}
			}
			if (normalize) {
				normalize(impacts);
			}
		}
		return ret;
	}
	
	private static void normalize(Map<String, Double> m) {
		double total = 0.0;
		for (double v : m.values()) {
			total += v;
		}
		for (Map.Entry<String, Double> e : m.entrySet()) {
			e.setValue(e.getValue() / total);
		}
	}
	
	private static String o(Object o) {
		System.out.println(o);
		return o.toString();
	}
	
	private static void output(int indent, String content) {
		for (int i = 0; i < indent; i++) {
			System.out.print("\t");
		}
		System.out.println(content);
	}
}
