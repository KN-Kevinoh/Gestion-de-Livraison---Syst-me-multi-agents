package agents;

import GUI.ConsommateurGUI;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ConsommateurAgent extends GuiAgent {
	
	private transient ConsommateurGUI gui; //Interface graphique
	
	/**
	 * 
	 * Agent qui est exécuté une fois que l'agent est instancié et déployé
	 * C'est la première méthode que l'agent exécute
	 */
	@Override
	protected void setup() {
		if(getArguments().length >=  1) {
			gui = (ConsommateurGUI) getArguments()[0];
			gui.setConsommateurAgent(this);
		}
		
		//On crée des comportements
		
		/*
		 * Si on veut exécuter plusieurs comportements en parallèle, on utilise parallelBehaviour
		 */
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		
		// Est appellé tout le temps (s'exécute à l'infini)
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				//System.out.println("ici2");
				MessageTemplate messageTemplate = 
						MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
								MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
										MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.AGREE),
												MessageTemplate.MatchPerformative(ACLMessage.REFUSE)))
								);
						
				ACLMessage aclMessage = receive(messageTemplate);
				if(aclMessage != null) {
					System.out.println("----------------------------");
					System.out.println("Reception du message");
					System.out.println(aclMessage.getContent());
					System.out.println(aclMessage.getSender().getName());
					System.out.println(aclMessage.getPerformative());
					System.out.println(aclMessage.getLanguage());
					System.out.println(aclMessage.getOntology());
					System.out.println("------------------------------");
					
					switch (aclMessage.getPerformative()) {
					case ACLMessage.CONFIRM:
						gui.logMessage(aclMessage);
						break;
						
					case ACLMessage.INFORM:
						if(aclMessage.getContent().contains("//")) {
							System.out.println("ici1");
							System.out.println(aclMessage.getContent());
							gui.recevoirProposition(aclMessage);
						} else {
							gui.logMessage(aclMessage);
						}
						break;
						
					case ACLMessage.FAILURE:
						gui.logMessage(aclMessage);
						break;

					default:
						break;
					}
				}
				else
					block();
			}
		});
	}

	/**
	 * 
	 * A utilser lorsqu'on a un agent qui utilise une interface graphique
	 */
	@Override
	public void onGuiEvent(GuiEvent params) {
		// TODO Auto-generated method stub
		if(params.getType() == 1) {
			String article = params.getParameter(0).toString();
			String prixMax = params.getParameter(1).toString();
			String qnte = params.getParameter(2).toString();
			ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
			aclMessage.setContent(article+"---"+prixMax+"---"+qnte);
			aclMessage.addReceiver(new AID("Acheteur", AID.ISLOCALNAME));
			send(aclMessage);
		} else if(params.getType() == 2) {
			String article = params.getParameter(0).toString();
			String qnte = params.getParameter(1).toString();
			String vendeur = params.getParameter(2).toString();
			ACLMessage aclMessage = new ACLMessage(ACLMessage.CONFIRM);
			aclMessage.setContent(article+"---"+qnte+"---"+vendeur);
			aclMessage.addReceiver(new AID("Acheteur", AID.ISLOCALNAME));
			send(aclMessage);
		}
	}
}
