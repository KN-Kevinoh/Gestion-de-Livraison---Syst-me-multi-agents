package agents;

import java.util.Random;

import GUI.LivreurGui;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class LivreurAgent extends GuiAgent {
	
	protected LivreurGui gui;
	
	@Override
	protected void setup() {
		if(getArguments().length == 1) {
			gui = (LivreurGui) getArguments()[0];
			gui.livreurAgent = this;
		}
		
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		
		parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
			
			@Override
			public void action() {
				// TODO Auto-generated method stub
				
				DFAgentDescription agentDescription = new DFAgentDescription();
				agentDescription.setName(getAID());
				ServiceDescription serviceDescription = new ServiceDescription();
				serviceDescription.setType("transaction");
				serviceDescription.setName("Livraison-articles");
				agentDescription.addServices(serviceDescription);
				try {
					DFService.register(myAgent, agentDescription);
					gui.logMessage("Agent " + myAgent.getLocalName() + " déployé");
				} catch (FIPAException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				// TODO Auto-generated method stub
				ACLMessage aclMessage = receive();
				if(aclMessage != null) {
					switch (aclMessage.getPerformative()) {
						case ACLMessage.CFP:
							
							ACLMessage reply = aclMessage.createReply();
							reply.setPerformative(ACLMessage.PROPOSE);
							Random rnd = new Random();
							reply.setContent(aclMessage.getContent() + "--" + String.valueOf(1000 + rnd.nextInt(1000)) + "--" + myAgent.getName());
							send(reply);
							
							break;
						case ACLMessage.ACCEPT_PROPOSAL:
							
							reply = aclMessage.createReply();
							reply.setPerformative(ACLMessage.CONFIRM);
							reply.setContent(aclMessage.getContent());
							send(reply);
							
							break;
						default:
							break;
					}
				} else {
					block();
				}
			}
		});
	}

	@Override
	protected void onGuiEvent(GuiEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void takeDown() {
		// TODO Auto-generated method stub
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
