## JVM相关

类的实例化顺序，比如父类静态数据，构造函数，字段，子类静态数据，构造函数，字段，他们的执行顺序
JVM内存分代
Java 8的内存分代改进
JVM垃圾回收机制，何时触发MinorGC等操作
jvm中一次完整的GC流程（从ygc到fgc）是怎样的，重点讲讲对象如何晋升到老年代，几种主要的jvm参数等
你知道哪几种垃圾收集器，各自的优缺点，重点讲下cms，g1
新生代和老生代的内存回收策略
Eden和Survivor的比例分配等
深入分析了Classloader，双亲委派机制
JVM的编译优化
对Java内存模型的理解，以及其在并发中的应用
指令重排序，内存栅栏等
OOM错误，stackoverflow错误，permgen space错误
JVM常用参数
tomcat结构，类加载器流程
volatile的语义，它修饰的变量一定线程安全吗
g1和cms区别,吞吐量优先和响应优先的垃圾收集器选择
说一说你对环境变量classpath的理解？如果一个类不在classpath下，为什么会抛出ClassNotFoundException异常，如果在不改变这个类路径的前期下，怎样才能正确加载这个类？
说一下强引用、软引用、弱引用、虚引用以及他们之间和gc的关系