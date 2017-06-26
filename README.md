# thucp

## 什么是thucp

`thucp`是一组基于Java开发的临床路径挖掘及分析工具集。

`临床路径`是针对特定病种的标准化诊疗计划，它已经成为一项重要的`医疗管理工具`，广泛用于提升预期疗效、控制医疗成本、减少资源浪费。在中国，自2009年起，国家卫计委先后发布并推广了超过1,000个病种的临床路径表单。当前，临床路径主要由专家研讨制定，然后交由各地具体实施，这类临床路径在实际应用中往往存在三个问题：1) 静态不变，更新费时费力；2) 实践性差，部署复杂，变异过多；3) 拓展性不足，难以应对繁多的病种（已知疾病10,000+）。

随着医疗信息化的快速发展，各地积累了大量的医疗数据，因此从数据中发现知识得到了越来越多的关注。我们的`thucp`工具箱围绕临床路径的不同应用领域，设计了一系列挖掘、分析工具，可以方便、快捷的从数据中发现有价值的信息，服务于临床路径的各类需求。`thucp`的输入文件主要包含两类数据，一是`医疗日志数据`，二是`标准临床路径模板`（特指国家卫计委发布的临床路径）。根据工具用途及输入输出要求，工具箱主要包含以下七方面功能：

1. 输入：医疗日志数据
   输出：临床路径模型
   功能描述：从医疗日志数据中，挖掘历史执行路径，客观反映数据中实际存在的常见医疗模式，模式包括医疗活动内容及活动间时序关系。前者由各种聚类算法（如主题模型、关联挖掘等）从繁杂的医疗日志数据中提取，然后交由过程挖掘算法抽取后者，从而生成最终的临床路径模型。
   潜在用途：所得模型可用于* 辅助临床路径的设计、再设计；

2. 输入：医疗日志数据
   输出：异常诊疗过程
   功能描述：
   潜在用途：
   
3. 输入：医疗日志数据 + 标准临床路径模板
   输出：本地化临床路径模板
   功能描述：
   潜在用途：
   
4. 输入：医疗日志数据 + 标准临床路径模板
   输出：合规性度量
   功能描述：
   潜在用途：
   
5. 输入：已执行医疗日志数据 + 临床路径模型
   输出：后续路径推荐
   功能描述：
   潜在用途：
   
6. 输入：医疗日志数据（可以多病种）
   输出：多维度临床路径模型差异比较
   功能描述：
   潜在用途： 
   
7. 输入：多个标准临床路径模板
   输出：临床路径模型
   功能描述：
   潜在用途：
   
注：医疗日志数据，除特殊说明外，均指单一病种数据。

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
