package com.questhelper.managers;

import com.questhelper.requirements.Requirement;
import com.questhelper.steps.ConditionalStep;
import net.runelite.api.Client;
import net.runelite.client.eventbus.EventBus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;


// TODO: This needs to work for sidebar requirements, as well as RequirementValidator? As well as active step requirement on overlay!
@Singleton
public class ActiveRequirementsManager
{
    private final Map<Requirement, Set<ConditionalStep>> requirements = new HashMap<>();

    @Inject
    private Client client;

    @Inject
    private EventBus eventBus;

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
        boolean isNewRequirement = !requirements.containsKey(requirement);
        requirements.computeIfAbsent(requirement, key -> {
            if (isNewRequirement)
            {
                requirement.register(client, eventBus, this);
            }
            return new HashSet<>();
        }).add(questStep);
    }

    /**
     * Removes a specific QuestStep from the specified Requirement.
     * If no more QuestSteps are associated with the Requirement, it will be unregistered.
     *
     * @param requirement The requirement to remove the QuestStep from.
     * @param questStep   The QuestStep to remove.
     */
    public void removeRequirement(Requirement requirement, ConditionalStep questStep)
    {
        Set<ConditionalStep> steps = requirements.get(requirement);
        if (steps != null)
        {
            steps.remove(questStep);
            if (steps.isEmpty())
            {
                requirements.remove(requirement);
                requirement.unregister(eventBus);
            }
        }
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
     * Clears all requirements and their associated QuestSteps.
     * Unregisters all requirements in the process.
     */
    public void clear()
    {
        requirements.keySet().forEach(requirement -> requirement.unregister(eventBus));
        requirements.clear();
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
