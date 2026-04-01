package duckcorp.factory;

import duckcorp.duck.Duck;
import duckcorp.machine.Machine;
import duckcorp.order.Order;
import duckcorp.stats.ProductionStats;
import duckcorp.stock.Stock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * L'usine du joueur. Gère le budget, les machines, le stock et la réputation.
 *
 * TODO (Ex5) :
 *   - Implémentez buyMachine(), maintainMachine(), runProduction(), fulfillOrder()
 *
 * TODO (Bonus 1) :
 *   - Implémentez endTurn()
 *
 * Le constructeur, les getters et notifyExpiredOrder() sont fournis.
 * @author Roussille Philippe <roussille@3il.fr>
 */
public class Factory {

    private double budget;
    private double reputation;
    private final Stock<Duck> stock;
    private final List<Machine> machines;
    private final ProductionStats stats;

    public Factory(double initialBudget) {
        this.budget     = initialBudget;
        this.reputation = 100.0;
        this.stock      = new Stock<>();
        this.machines   = new ArrayList<>();
        this.stats      = new ProductionStats();
    }

    // --- Getters fournis ---

    public double         getBudget()     { return budget; }
    public double         getReputation() { return reputation; }
    public Stock<Duck>    getStock()      { return stock; }
    public List<Machine>  getMachines()   { return Collections.unmodifiableList(machines); }
    public ProductionStats getStats()     { return stats; }

    // --- Méthodes fournies ---

    /**
     * Signale qu'une commande a expiré : pénalise la réputation et met à jour les stats.
     * Appelée par Game à chaque commande expirée. Ne pas modifier.
     */
    public void notifyExpiredOrder() {
        reputation = Math.max(0, reputation - 5);
        stats.recordExpiredOrder();
    }

    /**
     * Calcule le score final du joueur.
     * Formule : budget + réputation × 80 + commandesHonorées × 200 − commandesExpirées × 100
     */
    public int computeScore() {
        return (int) (budget
                + reputation * 80
                + stats.getTotalOrders() * 200
                - stats.getOrdersExpired() * 100);
    }

    // --- TODO (Ex5) ---

    /**
     * Achète une machine si le budget est suffisant.
     * Déduit le coût d'achat du budget et ajoute la machine à la liste.
     *
     * @return true si l'achat a réussi, false si budget insuffisant
     */
    public boolean buyMachine(Machine machine) {
        if (budget >= machine.getPurchaseCost()) {
            budget -= machine.getPurchaseCost();
            machines.add(machine);
            return true;
        }
        return false;
    }

    /**
     * Effectue la maintenance d'une machine si le budget est suffisant.
     * Déduit le coût de maintenance et appelle machine.maintain().
     *
     * @return true si la maintenance a réussi, false si budget insuffisant
     */
    public boolean maintainMachine(Machine machine) {
        if (budget >= machine.getMaintenanceCost()) {
            budget -= machine.getMaintenanceCost();
            machine.maintain();
            return true;
        }
        return false;
    }

    /**
     * Lance la production de toutes les machines pour ce tour.
     * Chaque machine produit autant de canards que sa capacité.
     * Les canards sont ajoutés au stock et retournés dans une liste.
     *
     * Conseil : déléguez à machine.produceDuck() — ne faites pas de instanceof.
     * Mettez à jour les stats via stats.recordProduction().
     *
     * @return la liste de tous les canards produits ce tour
     */
    public List<Duck> runProduction() {
        List<Duck> producedThisTurn = new ArrayList<>();
        for (Machine m : machines) {
            for (int i = 0; i < m.getCapacity(); i++) {
                Duck d = m.produceDuck();
                stock.add(d);
                producedThisTurn.add(d);
            }
        }
        stats.recordProduction(producedThisTurn);
        return producedThisTurn;
    }

    /**
     * Tente d'honorer une commande.
     * Si le stock est suffisant :
     *   - retire les canards du stock (les moins bons en premier, triés par qualité croissante)
     *   - crédite le budget du montant de la commande
     *   - met à jour la réputation selon la qualité moyenne des canards expédiés :
     *       qualité moy. >= 70 → +3
     *       qualité moy. >= 50 → +1
     *       qualité moy. <  50 → 0  (pas de bonus)
     *   - marque la commande comme honorée
     *   - met à jour les stats
     *
     * @return true si la commande a été honorée, false sinon
     */
    public boolean fulfillOrder(Order order) {
        if (order.canBeFulfilled(stock)) {

            List<Duck> shipped = stock.remove(order.getDuckType(), order.getQuantity());

            budget += order.getTotalValue();

            double sum = 0;
            for (Duck d : shipped) sum += d.getQualityScore();
            double average = sum / shipped.size();

            if (average >= 70) reputation += 3;
            else if (average >= 50) reputation += 1;

            if (reputation > 100) reputation = 100;

            order.fulfill();
            stats.recordSale(order);
            return true;
        }
        return false;
    }

    // --- TODO (Bonus 1) ---

    /**
     * Fin de tour : dégrade toutes les machines.
     * Pour chaque machine en état critique après dégradation (needsMaintenance()),
     * pénalise la réputation de 5 points.
     */
    public void endTurn() {
        for (Machine m : machines) {
            m.degrade();
            if (m.needsMaintenance()) {
                reputation = Math.max(0, reputation - 5);
            }
        }
    }
}
