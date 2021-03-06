/**
 * DE.java
 * @author Antonio J. Nebro
 * @version 1.0  
 */

package jmetal.metaheuristics.singleObjective.differentialEvolution;

import java.util.Comparator;

import jmetal.base.Algorithm;
import jmetal.base.Operator;
import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.base.operator.comparator.ObjectiveComparator;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.Ranking;

/**
 * This class implements a differential evolution algorithm. 
 */
public class DE extends Algorithm {
	 /**
   * stores the problem  to solve
   */
  private Problem  problem_;        
  
  /**
  * Constructor
  * @param problem Problem to solve
  */
  public DE(Problem problem){
    this.problem_ = problem;                        
  } // gDE
  
  /**   
   * Runs of the DE algorithm.
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution  
    * @throws JMException 
   */  
   public SolutionSet execute() throws JMException, ClassNotFoundException {
     int populationSize ;
     int maxEvaluations ;
     int evaluations    ;
     
     SolutionSet population          ;
     SolutionSet offspringPopulation ;
          
     Operator selectionOperator ;
     Operator crossoverOperator ;
               
     Comparator  comparator ;
     comparator = new ObjectiveComparator(0) ; // Single objective comparator
     
     // Differential evolution parameters
     int r1    ;
     int r2    ;
     int r3    ;
     int jrand ;

     Solution parent[] ;
     
     //Read the parameters
     populationSize = ((Integer)this.getInputParameter("populationSize")).intValue();
     maxEvaluations  = ((Integer)this.getInputParameter("maxEvaluations")).intValue();     
    
     selectionOperator = operators_.get("selection");   
     crossoverOperator = operators_.get("crossover") ;
     
     //Initialize the variables
     population  = new SolutionSet(populationSize);        
     evaluations = 0;                

     // Create the initial solutionSet
     Solution newSolution;
     for (int i = 0; i < populationSize; i++) {
       newSolution = new Solution(problem_);                    
       problem_.evaluate(newSolution);            
       problem_.evaluateConstraints(newSolution);
       evaluations++;
       population.add(newSolution);
     } //for       
   
     // Generations ...
     population.sort(comparator) ;
     while (evaluations < maxEvaluations) {
       
       // Create the offSpring solutionSet      
       offspringPopulation  = new SolutionSet(populationSize);        

       //offspringPopulation.add(new Solution(population.get(0))) ;	
      
       for (int i = 0; i < populationSize; i++) {   
         // Obtain parents. Two parameters are required: the population and the 
         //                 index of the current individual
         parent = (Solution [])selectionOperator.execute(new Object[]{population, i});

         Solution child ;
         
         // Crossover. Two parameters are required: the current individual and the 
         //            array of parents
         child = (Solution)crossoverOperator.execute(new Object[]{population.get(i), parent}) ;

         problem_.evaluate(child) ;

         evaluations++ ;
         
         if (comparator.compare(population.get(i), child) < 0) 
           offspringPopulation.add(new Solution(population.get(i))) ;
         else
           offspringPopulation.add(child) ;
       } // for
       
       // The offspring population becomes the new current population
       population.clear();
       for (int i = 0; i < populationSize; i++) {
         population.add(offspringPopulation.get(i)) ;
       }
       offspringPopulation.clear();
       population.sort(comparator) ;
     } // while
     
     // Return a population with the best individual
     SolutionSet resultPopulation = new SolutionSet(1) ;
     resultPopulation.add(population.get(0)) ;
     
     System.out.println("Evaluations: " + evaluations ) ;
     return resultPopulation ;
   } // execute
} // gDE
