package com.ey.advisory.common.async;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 * This class is responsible for fetching Messages from a Message Source
 * (like DB or a message queue like Kafka), locating appropriate Job/Task
 * instances that can process the message and distributing these tasks
 * to the free threads in the Executor Pool. The implementation of this
 * class typically should start an infinite periodic loop (preferably
 * in a background thread)`that fetches messages from an appropriate 
 * message source and performs the task lookup and distribution.
 * 
 * The instance of this class doesn't have to be single threaded. But,
 * it sure does need to understand the Executor Thread Pool and should fetch
 * messages from the message source only when there are free threads to
 * execute in the pool. This is a very important point as several 
 * instances of this application may be started and each instance 
 * manages its own Executor thread pool. So, if messages are fetched
 * and kept in memory, when there are no free threads in the executor 
 * pool, these messages would take an awfully long time before they get
 * delivered to executor threads for processing.
 * 
 * @author Sai.Pakanati
 *
 */
public interface TaskFetcherAndDistributor {

	/**
	 * Start an infinite periodic loop of fetching messages and
	 * allocating tasks.
	 */
	public void fetchAndAllocateTasks();
	
	public CopyOnWriteArrayList<String> getTaskTypeList();

}