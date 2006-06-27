public abstract class AbstractClass implements InterfaceDef {

    public interface Composite {}
    public class ImmutableComposite implements Composite { Composite anonymous = new Composite(){}; }
    public static class SharedComposite implements Composite {}

}
