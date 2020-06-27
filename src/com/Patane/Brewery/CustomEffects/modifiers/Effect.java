package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import org.yaml.snakeyaml.error.YAMLException;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Collections.BrEffectCollection;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.annotations.ParseField;
import com.Patane.util.general.Check;
import com.Patane.util.general.StringsUtil;

@ClassDescriber(
		name="effect",
		desc="Activates a different, pre-existing effect when the attached effect is activated.")
public class Effect extends Modifier{
	@ParseField(desc="Brewery effect to activate.")
	public BrEffect effect;
	
	public Effect() {
		super();
	}
	
	public Effect(Map<String, String> fields) {
		super(fields);
	}
	

	@Override
	protected void populateFields(Map<String, String> fields) {
		// First checks if the effect name is actually present.
		String effectName = fields.get("effect");
		Check.notNull(effectName, "'effect' field is missing.");
		
		if(BrEffectCollection.isProcessing(effectName)) {
			throw new YAMLException("'"+effectName+"' effect is currently being processed. "
					+ "Does this effect have itself as its modifier or does it loop with one to many other effects with the EFFECT modifier? "
					+ "Please check your YML files.");
		}
//		BrEffect tempEffect;
		
		// If the effect is not already created, throw exception with message
		if(!Brewery.getEffectCollection().hasItem(effectName))
			throw new IllegalArgumentException("'"+effectName+"' is not an Effect. Please create this effect before using it as a modifier.");
//		// If the item has already been fully loaded, simply grab it from the collection.
//		if(Brewery.getEffectCollection().hasItem(effectName))
//			tempEffect = Brewery.getEffectCollection().getItem(effectName);
//		
//		// Otherwise, retrieve the item from the effects.yml NOW.
//		// This will add it to the collection (if fully loaded) and stop it from being reloaded later.
//		else
//			tempEffect = BrEffectYML.retrieve(YAMLFile.getSectionAndWarn(BrEffect.YML().getPrefix(), effectName), null);
//		
		effect = Brewery.getEffectCollection().getItem(effectName);
		
		// Creates a new BrEffect clones the previous, however it has no filter and doesnt ignore the user.
		// This is done because the first effect already filters and ignores user. It doesnt need to do it twice.
//		effect = new BrEffect(tempEffect.getName(), tempEffect.getModifier(), tempEffect.getTrigger(), tempEffect.getRadius(), 
//				null, tempEffect.getParticleEffect(), tempEffect.getSoundEffect(), tempEffect.getPotions(), tempEffect.getTag(), false);
		// If the effect failed to load, it prints this error.
//		Check.notNull(effect, "Effect is missing. Did it fail to load?");
		if(!effect.isComplete())
			throw new IllegalArgumentException("'"+effect.getName()+"' must be complete for this modifier. Please update the item to add the following values: "+StringsUtil.stringJoiner(effect.getIncomplete(), "&c, &7"));
			
	}
	public Effect(BrEffect effect) {
		this.effect = effect;
		construct();
	}

	/* 
	 * ================================================================================
	 */
	
	@Override
	public void modify(ModifierInfo info) {
		effect.getTrigger().execute(effect, info.getTargeter(), info.getTarget());
	}

}
