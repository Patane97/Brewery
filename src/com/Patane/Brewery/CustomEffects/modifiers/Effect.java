package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import org.yaml.snakeyaml.error.YAMLException;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Collections.BrEffectCollection;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.BrEffectYML;
import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.YAML.types.YAMLFile;
import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.annotations.ParseField;
import com.Patane.util.general.Check;

@ClassDescriber(
		name="effect",
		desc="Activates a different, pre-existing effect when the attached effect is activated.")
public class Effect extends Modifier{
	@ParseField(desc="Brewery effect to activate. This effect must be complete.")
	private BrEffect effect;
	
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
			throw new YAMLException(String.format("&7%s &eeffect is currently being processed. "
					+ "Does this effect have itself as its modifier or does it loop with one to many other effects with the EFFECT modifier? "
					+ "Please check your YML files.", effectName));
		}
//		BrEffect tempEffect;
		
		// Check if the effect is already created/saved
		if(!Brewery.getEffectCollection().hasItem(effectName)) {
			// Attempt to retrieve the effect now if it cant be found
			BrEffect possibleEffect = BrEffectYML.retrieve(YAMLFile.getSectionAndWarn(BrEffect.YML().getPrefix(), effectName), null);
			// If it cannot be found or failed, throw exception
			if(possibleEffect == null)
				throw new NullPointerException(String.format("&7%s &cis either not an effect or failed to be created.", effectName));
			// If it was successfully created, save effect as the found effect.
			effect = possibleEffect;
		}
		// Otherwise, simply retrieve the item from the collection
		else
			effect = Brewery.getEffectCollection().getItem(effectName);
		
		if(!effect.isComplete())
			throw new IllegalArgumentException(String.format("&7%s &cis not a complete effect. Please compelete this effect before using it as a modifier.", effect.getName()));
			
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
