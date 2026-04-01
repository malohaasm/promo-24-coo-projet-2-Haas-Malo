package duckcorp.stock;

import duckcorp.duck.Duck;
import duckcorp.duck.DuckType;

import java.util.*;

/**
 * Stock générique de canards.
 *
 * TODO (Ex3) :
 *   - Implémentez remove(), count(), countDefective(), countByType()
 *
 * Les méthodes add(), getAll() et total() sont fournies.
 *
 * @param <T> type de canard stocké (doit étendre Duck)
 * @author Roussille Philippe <roussille@3il.fr>
 */
public class Stock<T extends Duck> {

    private final List<T> items = new ArrayList<>();

    // --- Méthodes fournies ---

    /** Ajoute un canard au stock. */
    public void add(T duck) {
        items.add(duck);
    }

    /** Retourne une vue non modifiable de tous les canards en stock. */
    public List<T> getAll() {
        return Collections.unmodifiableList(items);
    }

    /** Retourne le nombre total de canards en stock. */
    public int total() {
        return items.size();
    }

    // --- TODO ---

    /**
     * Retire exactement {@code count} canards du type {@code type} du stock
     * et les retourne dans une liste.
     *
     * @param type  le type de canard à retirer
     * @param count le nombre à retirer
     * @return la liste des canards retirés
     * @throws IllegalStateException si le stock ne contient pas assez de canards du type demandé
     *
     * Conseil : parcourez items en une seule passe.
     * Attention à la signature de retour : elle doit conserver le type générique T.
     */
    public List<T> remove(DuckType type, int count) {

        if (count(type) < count) {
            throw new IllegalStateException("Stock insuffisant : " + count + " " + type + " demandés.");
        }

        List<T> removed = new ArrayList<>();
        Iterator<T> it = items.iterator();

        while (it.hasNext() && removed.size() < count) {
            T item = it.next();
            if (item.getType() == type) {
                removed.add(item);
                it.remove();
            }
        }
        return removed;
    }

    /**
     * Retourne le nombre de canards du type {@code type} présents dans le stock.
     *
     * @param type le type à compter
     */
    public int count(DuckType type) {
        //
        int total = 0;
        for (T item : items) {
            if (item.getType() == type) {
                total++;
            }
        }
        return total;
    }

    /**
     * Retourne le nombre de canards défectueux dans le stock.
     * Un canard est défectueux si isDefective() retourne true.
     *
     * Conseil : appelez isDefective() plutôt que de comparer le score manuellement.
     */
    public int countDefective() {
        int total = 0;
        for (T item : items) {
            if (item.isDefective()) {
                total++;
            }
        }
        return total;
    }

    /**
     * Retourne une Map associant chaque DuckType au nombre de canards
     * de ce type présents dans le stock.
     *
     * Conseil : construisez la map en une seule passe sur items.
     * Tous les types doivent apparaître dans la map (avec 0 si absent).
     */
    public Map<DuckType, Integer> countByType() {
        Map<DuckType, Integer> map = new HashMap<>();

        for (DuckType type : DuckType.values()) {
            map.put(type, 0);
        }

        for (T item : items) {
            DuckType type = item.getType();
            map.put(type, map.get(type) + 1);
        }

        return map;
    }
}
