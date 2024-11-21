package com.questhelper.managers;

import com.questhelper.bank.QuestBank;
import com.questhelper.panel.QuestHelperPanel;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.ConditionalStep;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;


// TODO: This needs to work for sidebar requirements, as well as RequirementValidator? As well as active step requirement on overlay!
@Singleton
public class ActiveRequirementsManager
{
    private final Map<Requirement, Set<ConditionalStep>> requirements = new HashMap<>();
    private final Set<Requirement> sidebarRequirements = new HashSet<>();

    @Inject
    private Client client;

    @Inject
    ClientThread clientThread;

    @Inject
    private EventBus eventBus;

    @Setter
    private QuestHelperPanel panel;

    @Setter
    @Getter
    private QuestBankManager questBankManager;

    /**
     * Adds a QuestStep to the specified Requirement.
     * If the Requirement is not already in the map, it will be registered.
     *
     * @param requirement The requirement to associate with a QuestStep.
     * @param questStep   The QuestStep to associate.
     */
    public void addRequirement(Requirement requirement, ConditionalStep questStep)
    {
        // Register the requirement if it's not already present
        boolean isNewRequirement = !isRequirementActive(requirement);
        requirements.computeIfAbsent(requirement, key -> {
            if (isNewRequirement)
            {
                requirement.register(client, clientThread, eventBus, this);
            }
            return new HashSet<>();
        }).add(questStep);
    }

    /**
     * Adds a Requirement to the sidebar.
     * If the Requirement is not already registered, it will be registered.
     *
     * @param requirement The requirement to display in the sidebar.
     */
    public void addSidebarRequirement(Requirement requirement)
    {
        if (!isRequirementActive(requirement) && sidebarRequirements.add(requirement))
        {
            requirement.register(client, clientThread, eventBus, this);
        }
    }

    /**
     * Removes a Requirement from the sidebar.
     * If no more QuestSteps or sidebar associations exist, the Requirement will be unregistered.
     *
     * @param requirement The requirement to remove from the sidebar.
     */
    public void removeSidebarRequirement(Requirement requirement)
    {
        if (sidebarRequirements.remove(requirement) && !isRequirementActive(requirement))
        {
            requirement.unregister(eventBus);
        }
    }

    /**
     * Clears all requirements and their associated QuestSteps and sidebar entries.
     * Unregisters all requirements in the process.
     */
    public void shutDown()
    {
        requirements.keySet().forEach(req -> req.unregister(eventBus));
        sidebarRequirements.forEach(req -> req.unregister(eventBus));

        requirements.clear();
        sidebarRequirements.clear();
    }

    /**
     * Removes all QuestSteps for a specific Requirement.
     * If the Requirement is completely removed, it will be unregistered.
     *
     * @param requirement The requirement to remove.
     */
    public void removeRequirement(Requirement requirement)
    {
        if (requirements.containsKey(requirement))
        {
            requirements.remove(requirement);
            requirement.unregister(eventBus);
        }
    }

    public void sendUpdateOfRequirementToSteps(Requirement requirement)
    {
        if (sidebarRequirements.contains(requirement))
        {
            panel.updateRequirement(client, requirement);
        }

        Set<ConditionalStep> steps = requirements.get(requirement);
        if (steps == null) return;
        steps.forEach(ConditionalStep::revalidateOnRequirementChanged);
    }

    /**
     * Gets all QuestSteps associated with a specific Requirement.
     *
     * @param requirement The requirement to query.
     * @return A set of QuestSteps or an empty set if none are found.
     */
    public Set<ConditionalStep> getStepsForRequirement(Requirement requirement)
    {
        return requirements.getOrDefault(requirement, Collections.emptySet());
    }

    /**
     * Returns all sidebar requirements.
     *
     * @return A set of requirements that are displayed in the sidebar.
     */
    public Set<Requirement> getSidebarRequirements()
    {
        return Collections.unmodifiableSet(sidebarRequirements);
    }

    /**
     * Checks if a requirement is active (used in steps or the sidebar).
     *
     * @param requirement The requirement to check.
     * @return True if the requirement is active, false otherwise.
     */
    private boolean isRequirementActive(Requirement requirement)
    {
        return requirements.containsKey(requirement) || sidebarRequirements.contains(requirement);
    }

    /**
     * Returns all active requirements.
     *
     * @return A map of all active requirements and their QuestSteps.
     */
    public Map<Requirement, Set<ConditionalStep>> getAllRequirements()
    {
        return Collections.unmodifiableMap(requirements);
    }
}
