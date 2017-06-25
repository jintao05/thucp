package org.processmining.plugins.heuristicsnet.miner.heuristics.miner;

//import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
//import org.deckfour.xes.model.XLog;
//import org.processmining.contexts.uitopia.UIPluginContext;
//import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
//import org.processmining.framework.plugin.annotations.Plugin;
//import org.processmining.framework.plugin.annotations.PluginVariant;
//import org.processmining.models.heuristics.HeuristicsNet;
//import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.gui.ParametersPanel;
//
//@Plugin(name = "Heuristics Miner", parameterLabels = { "Log", "Settings" }, returnLabels = { "Mined Models" }, returnTypes = { HeuristicsNet.class }, userAccessible = true, help = "Heuristics Miner Plug-in")
//public class HeuristicsMinerPlugin {
//	//TODO - Add documentation
//
//	//TODO - Add a help
//	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "A.J.M.M. Weijters", email = "a.j.m.m.weijters@tue.nl", website = "http://is.tm.tue.nl/staff/aweijters")
//	@PluginVariant(variantLabel = "User-defined settings", requiredParameterLabels = { 0 })
//	public static HeuristicsNet run(UIPluginContext context, XLog log) {
//		//TODO - Build different plug-ins that already receive log infos!!!
//		//Note that, the default classifier are been used at the moment
//
//		ParametersPanel parameters = new ParametersPanel();
//		
//		InteractionResult result = context.showConfiguration("Heuristics Miner Parameters", parameters);
//		if (result.equals(InteractionResult.CANCEL)) {
//			context.getFutureResult(0).cancel(true);
//		}
//
//		//XLogInfo logInfo = XLogInfoFactory.createLogInfo(log);
//		HeuristicsMiner hm = new HeuristicsMiner(context, log, parameters.getSettings());
//		return hm.mine();
//	}
//}