# thucp

## What is thucp
thucp is a set of java-based tools for Clinical Pathway (CP) mining and analysis. 

A Clinical Pathway refers to a standard diagnosis and treatment plan for specific disease. It is becoming one of the most important management tools for clinical quality imporvement, expense control and resource regulation. In China, more that 1000 CPs have been issued 
from 2009. However, most of current CPs are designed by experts mutually, so that they are always static and non-adaptive for clinical practice. 

With the rapid growth of clinical informatics, data-driven methods that discovering knowledge from clinical data are receiving more and more attention. Our toolbox thucp presents a series of data mining and analysis approachs for various CP applications include: 

  1. Execution Clinical Pathway Mining. An execution CP of a disease represents the most common clinical behaviors in the historical data. There are two core components for it: the kinds of behaviors and the relations between the behaviors. To achieve this goal, different clustering algorithms (k-means, topic modeling, association rule mining, etc) are firstly used for abstracting the complex clinical activities. Then, process mining methods are applied on these abstractions for deriving temporal relations. 
  
  2. Compliance Checking. Given an expert-designed or data-driven CP model, how can we evaluate the conformity degree between a patient record and the model. Various temporal analysis and process replay methods are presented for this task. 
  
  3. Clinical Desision Support. 
  
  4. Clinical Pathway Onotology. 

## How to install it
