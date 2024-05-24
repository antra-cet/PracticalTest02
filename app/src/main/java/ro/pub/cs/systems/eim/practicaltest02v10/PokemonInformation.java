package ro.pub.cs.systems.eim.practicaltest02v10;

public class PokemonInformation {

    private final String ability;
    private final String types;

    public PokemonInformation(String ability, String types) {
        this.ability = ability;
        this.types = types;
    }

    public String getAbility() {
        return ability;
    }

    public String getTypes() {
        return types;
    }

    @Override
    public String toString() {
        return "PokemonInformation{" +
                "ability='" + ability + '\'' +
                ", types='" + types + '\'' +
                '}';
    }

}