package jaywalker.report;

import java.util.HashMap;
import java.util.Map;

public class TagMap {

	private DependencyModel dependencyModel = new DependencyModel();

	private CollisionModel collisionModel = new CollisionModel();

	private Map map = new HashMap();

	public TagMap() {
		final ContainerDependencyTag containerDependencyTag = new ContainerDependencyTag(
				dependencyModel);
		put("archive", "metrics", containerDependencyTag);
		put("archive", "resolved", containerDependencyTag);
		put("archive", "cycle", new ContainerCyclicDependencyTag(
				dependencyModel));

		final PackageDependencyTag packageDependencyTag = new PackageDependencyTag(
				dependencyModel);
		put("package", "metrics", packageDependencyTag);
		put("package", "resolved", packageDependencyTag);
		put("package", "cycle", new PackageCyclicDependencyTag(dependencyModel));

		put("class", "collision", new CollisionTag(collisionModel));
		put("class", "conflict",
				new SerialVersionUidConflictTag(collisionModel));
		put("class", "unresolved", new UnresolvedClassNameDependencyTag(
				dependencyModel));
		put("class", "cycle", new ElementCyclicDependencyTag(dependencyModel));
	}

	public void put(String scope, String type, Tag tag) {
		map.put(toKey(scope, type), tag);
	}

	public Tag get(String scope, String type) {
		return (Tag) map.get(toKey(scope, type));
	}

	private String toKey(String scope, String type) {
		return scope + "-" + type;
	}

}
