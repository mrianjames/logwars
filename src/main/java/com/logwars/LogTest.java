package com.logwars;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.monitoring.runtime.instrumentation.AllocationRecorder;
import com.google.monitoring.runtime.instrumentation.Sampler;
import com.oaktree.core.logging.LowLatencyLogManager;

/**
 * A tester of slf4j logging implementations using a variety of "normal" operations in a low latency
 * and concurrent application:
 * 1) Logging text to console in a useful structure comprising of time to ms precision, thread id, thread
 * 		name, logging level and text. Some of this information is not available in all impls - most provide
 * 		only thread id or name, however both are required for serious mt applications.
 * 2) Checking if logging is enabled - the most called method in lots of applications. It checks if a 
 * 		logging level is enabled to decide if we need to build the log statement at all. 
 * 3) Logger creation speed. This test checks that creating loggers and setting them up is not a time
 * 		consuming business. This is needed for scenarios where objects with unique loggers are created
 * 		dynamically.
 * 4) Logger access speed. This tests retreiving the logger from the log manager/factory. 
 * 
 * All tests are run in single-threaded and multi-threaded mode to attain a snapshot of performance
 * you may expect in a real world concurrent application.
 * 
 * @author Oak Tree Designs Ltd
 *
 */
public class LogTest {
	
	//static Logger logger;
	static int LOOPS = 10;
	static int TESTS = 10000;
	final static String MESSAGE = "Excaliber";
	static DecimalFormat format = new DecimalFormat("#,###.##");
	static String LOGGER_NAME = LogTest.class.getName();
	static int THREADS = 2;
	private final static Logger logger = LoggerFactory.getLogger(LogTest.class);


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String recal = System.getProperty("record.allocations");
		boolean recordAllocations = false;
		if (recal != null && recal.length() > 0) {
			recordAllocations = Boolean.valueOf(recal);
		}
		
		
		Writer writer = null;
		try {
			if (args.length > 1) {
				THREADS = Integer.valueOf(args[1]);
			}
			try {
				writer = new FileWriter("../logs/result.txt",true);
			} catch (FileNotFoundException e) {
				writer = new FileWriter("logs/result.txt",true);
			}
			final long totalSize[] = new long[]{0};
			//writer = new PrintWriter(System.out);
			writer.write("#################################\n");
			writer.write("Test: " + args[0] + " threads " + THREADS + " @ "+Text.renderTime(System.currentTimeMillis())+"\n");
			if (recordAllocations) {
				writer.write("Recording Allocations: true\n");
				  AllocationRecorder.addSampler(new Sampler() {
										
					    public void sampleAllocation(int count, String desc,
					      Object newObj, long size) {
					      totalSize[0] += size;
					    }
					  });
			}
			writer.write("#################################\n");
			
			 
			
			long start = System.nanoTime();
			double avg = doLoggingSpeedTest(1);
			double avg2 = doEnabledSpeedTest(1);
			double avg3 = doLoggerCreationSpeedTest(1);
			double avg4 = doLoggerRetreivalSpeedTest(1);
			double avg5 = doLoggingSpeedTest(THREADS);
			double avg6 = doEnabledSpeedTest(THREADS);
			double avg7 = doLoggerCreationSpeedTest(THREADS);
			double avg8 = doLoggerRetreivalSpeedTest(THREADS);
			long end = System.nanoTime();
			double totald = (end-start)/1000000d;
	
			writer.write("LOG_SPEED: " + TESTS + " in " + format.format(avg)		+ " ms.\n");
			writer.write("IS_ENABLED: " + TESTS + " in " + format.format(avg2)	+ " ms.\n");
			writer.write("LOGGER_CRE: " + TESTS + " in " + format.format(avg3)	+ " ms.\n");
			writer.write("LOGGER_RET: " + TESTS + " in " + format.format(avg4)	+ " ms.\n");
			writer.write("LOG_SPEED: " + THREADS + ": " + (TESTS * THREADS) + " in "	+ format.format(avg5) + " ms.\n");
			writer.write("IS_ENABLED: " + THREADS + ": " + (TESTS * THREADS)	+ " in " + format.format(avg6) + " ms.\n");
			writer.write("LOGGER_CRE: " + THREADS + ": in " + format.format(avg7) + " ms.\n");
			writer.write("LOGGER_RET: " + THREADS + ": " + (TESTS * THREADS)	+ " in " + format.format(avg8) + " ms.\n");
			writer.write("Duration: " + format.format(totald) + " ms.\n");
			writer.write("Allocation: " + totalSize[0] + " bytes.\n");
			writer.write("#################################\n");
			writer.flush();
			writer.close();
			Thread.sleep(2000);
			
		} catch (Exception e) {
			e.printStackTrace();
			if (writer != null) {
				try {
					writer.write(e.getMessage());
					writer.flush();
				} catch (IOException e1) {					
					e1.printStackTrace();
				}
			}
		}

		try {
			LowLatencyLogManager.stop();
		} catch (Throwable t){}
		System.exit(0);
	}

	/**
	 * Get hold of a named logger over and over. This will probably test the amounts of 
	 * contention that exist in the manager of loggers.
	 * @param threads
	 * @return
	 */
	private static double doLoggerRetreivalSpeedTest(int threads) {
		final double[] avgs = new double[threads];
		final Thread[] ts = new Thread[threads];
		final CountDownLatch latch = new CountDownLatch(threads);
		for (int p = 0; p < threads; p++) {
			final int x = p;
			ts[p] = new Thread(new Runnable() {
				public void run() {
					List<Double> times = new ArrayList<Double>();
					/*
					 * Test logger retreival speed.
					 */					
					for (int j = 0; j < LOOPS; j++) {
						long s = System.nanoTime();
						for (int i = 0; i < TESTS; i++) {
							GET_LOGGER();
						}
						long e = System.nanoTime();
						double d = (e - s) / 1000000d;
						if (j > 0) {
							times.add(d);
						}
					}
					double sum = 0;
					for (double d : times) {
						sum += d;
					}
					double avg = sum / (times.size());
					avgs[x] = avg;
					latch.countDown();
				}
			});
			ts[p].setName("LogThread" + x);
		}
		for (int p = 0; p < threads; p++) {
			ts[p].start();
		}

		try {
			latch.await();
		} catch (Exception e) {
		}
		System.out.println("TEST 4 complete");
		double avg = 0;
		double total = 0;
		for (double d : avgs) {
			total += d;
		}
		avg = total / (double) threads;
		return avg;
	}

	/**
	 * Dynaically creating loggers can be time consuming if written badly. Compare and contrast
	 * our loggers fortunes.
	 * @param threads
	 * @return
	 */
	private static double doLoggerCreationSpeedTest(int threads) {
		final double[] avgs = new double[threads];
		final Thread[] ts = new Thread[threads];
		final CountDownLatch latch = new CountDownLatch(threads);
		for (int p = 0; p < threads; p++) {
			final int x = p;
			ts[p] = new Thread(new Runnable() {
				public void run() {
					/*
					 * Test logger creation speed.
					 */
					List<Double> times = new ArrayList<Double>();
					times.clear();
					for (int j = 0; j < 1000; j++) {
						long s = System.nanoTime();
						//for (int i = 0; i < TESTS; i++) {
							Logger t = LoggerFactory.getLogger(""
									+ System.nanoTime());
						//}
						long e = System.nanoTime();
						t.info(String.valueOf(s)); //so not optimised out...
						double d = (e - s) / 1000000d;
						if (j > 0) {
							times.add(d);
						}
					}
					double sum = 0;
					for (double d : times) {
						sum += d;
					}
					double avg = sum / (times.size());
					avgs[x] = avg;
					latch.countDown();
				}
			});
			ts[p].setName("LogThread" + x);
		}
		for (int p = 0; p < threads; p++) {
			ts[p].start();
		}

		try {
			latch.await();
		} catch (Exception e) {
		}
		System.out.println("TEST 3 complete");
		double avg = 0;
		double total = 0;
		for (double d : avgs) {
			total += d;
		}
		avg = total / (double) threads;
		return avg;

	}

	/**
	 * Check the most used functionality - checking if logging is 
	 * enabled for a level on a logger. This tends to be called repeatedly so should
	 * be lightening quick.
	 * @param threads
	 * @return
	 */
	private static double doEnabledSpeedTest(int threads) {
		final double[] avgs = new double[threads];
		final Thread[] ts = new Thread[threads];
		final CountDownLatch latch = new CountDownLatch(threads);
		for (int p = 0; p < threads; p++) {
			final int x = p;
			ts[p] = new Thread(new Runnable() {
				public void run() {
					List<Double> times = new ArrayList<Double>();
					/*
					 * a test on if loggable checks.
					 */
					for (int j = 0; j < LOOPS; j++) {
						long s = System.nanoTime();
						for (int i = 0; i < TESTS; i++) {
							CHECK_ENABLED();
						}
						long e = System.nanoTime();
						double d = (e - s) / 1000000d;
						if (j > 0) {
							times.add(d);
						}
					}
					double sum = 0;
					for (double d : times) {
						sum += d;
					}
					double avg = sum / (times.size());
					avgs[x] = avg;
					latch.countDown();
				}

				
			});
			ts[p].setName("LogThread" + x);
		}
		for (int p = 0; p < threads; p++) {
			ts[p].start();
		}

		try {
			latch.await();
		} catch (Exception e) {
		}
		System.out.println("TEST 2 complete");
		double avg = 0;
		double total = 0;
		for (double d : avgs) {
			total += d;
		}
		avg = total / (double) threads;
		return avg;
	}

	/**
	 * Test basic logging of text, including formatting using a standard useful layout that
	 * include thread name AND ID (if available, only oaktree), logger level and time to 
	 * millisecond precision. This formatter needs to be threadsafe (in terms of dateformat).
	 * @param threads
	 * @return
	 */
	private static double doLoggingSpeedTest(int threads) {
		final double[] avgs = new double[threads];
		final Thread[] ts = new Thread[threads];
		final CountDownLatch latch = new CountDownLatch(threads);
		for (int p = 0; p < threads; p++) {
			final int x = p;
			ts[p] = new Thread(new Runnable() {
				public void run() {
					List<Double> times = new ArrayList<Double>();
					for (int j = 0; j < LOOPS; j++) {
						long s = System.nanoTime();
						for (int i = 0; i < TESTS; i++) {
							LOG(MESSAGE);
						}
						long e = System.nanoTime();
						double d = (e - s) / 1000000d;
						if (j > 0) {
							times.add(d);
						}
					}
					double sum = 0;
					for (double d : times) {
						sum += d;
					}
					double avg = sum / (times.size());
					avgs[x] = avg;
					latch.countDown();
				}

				
			});
			ts[p].setName("LogThread" + x);
		}
		long s = System.nanoTime();
		for (int p = 0; p < threads; p++) {
			ts[p].start();
		}

		try {
			latch.await();
		} catch (Exception e) {
		}
		long e = System.nanoTime();
		double totald = (e-s)/1000000d;
		System.out.println("TEST 1 complete: " + format.format(totald) + "ms.");
		double avg = 0;
		double total = 0;
		for (double d : avgs) {
			total += d;
		}
		avg = total / (double) threads;
		//return avg;
		return totald;
	}
	
	private static void LOG(String message) {
		logger.info(message);
	}
	static int i=0;
	private static void CHECK_ENABLED() {
		if (logger.isInfoEnabled()) {
			i++;
		}
	}
	private static void GET_LOGGER() {
		Logger logger = LoggerFactory.getLogger(LOGGER_NAME);
		logger.info(String.valueOf(i));
	}
}
