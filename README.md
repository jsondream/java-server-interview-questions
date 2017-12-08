## java基础   

Arrays.sort实现原理和Collection实现原理   
foreach和while的区别(编译之后)   
线程池的种类，区别和使用场景   
分析线程池的实现原理和线程的调度过程   
线程池如何调优   
线程池的最大线程数目根据什么确定   
动态代理的几种方式   
HashMap的并发问题   
了解LinkedHashMap的应用吗  
反射的原理，反射创建类实例的三种方式是什么？   
cloneable接口实现原理，浅拷贝or深拷贝   
Java NIO使用   
hashtable和hashmap的区别及实现原理，hashmap会问到数组索引，hash碰撞怎么解决?
* 答：以jdk1.7为示例，hashtable官方已经不推荐使用了，但是在没有concurrent包之前，hashtable还是做出了很大的贡献，首先hashtable和hashmap的底层都是hash表，数组加链表形式，hashtable线程是安全的，put和get方法都用了synchronized修饰，其次不允许存null值的，效率相对于hashmap要低一些。hashmap线程是不安全的，如果要实现线程安全，可以使用concurrentMap代替或者使用Collections工具类的synchronizeMap封装，哈希表有两种实现方式，一种是开放地址链表来实现，另一种是使用冲突链表，而hashmap使用的就是冲突链表实现的，也就是数组加链表的形式，数组一般用来存key经过hashcode方法的集合，默认容量初始化16个，注意的是容量跟元素个数大小是不一样的概念，源码告诉咋们，一般来说，容量的大小设置为元素个数除于0.75，然后加+1，为什么容量初始化设置为16个呢，原因是让数组的元素分布更加均匀一些，而不会产生更多的hash碰撞呢，如何实现更加均匀呢，就是调用hash函数，hash函数实现高位运算，也就是与运算，公式是key经过hashcode方法返回的值&（容量-1），数学公式：  index = HashCode（Key） & （Length - 1），而初始化容量最好是2次幂，这样分布的更加均匀一些，如果容量不够了，也就是元素个数超过了12个，他就会启动扩容机制，增加一倍的容量，代码是原容量左移动一位。扩容是怎么实现的呢？将原来的index重新分配，复制到新容器中，重新分配会耗相对较大的性能，所以一般建议是使用hashmap最好初始化容量，公式是：(元素个数/0.75)+1,这样是最好的，如果不确定容量，最好初始化一个相对较大的容量。put方法和get方法是怎么实现的呢？存元素的时候，先获取key,进行hashcode，得到index值，如果链表为空，则直接存入，而如果链表有元素，比较key的值，相同则覆盖，不相同则采用头插法，排在前面，node（hash，key，values，next，）,next属性指向旧节点。同理，get方法也是根据key，hashcode以后得到的值，找到对应的数组下标，然后通过比较key的形式获取element.
arraylist和linkedlist区别及实现原理
* 答：基于jdk1.7,ArrayList的底层是数组，而LinkedList的底层是双向链表。
* 先说接口，ArrayList的实现的接口有List接口，也就是说它是有序集合，支持存取顺序一致，其次还实现随机访问接口，也就是RandomAccess，但这个接口里面没有要实现的方法，它只是一种暗示，意思就是说数组是带下标的，访问元素的时间复杂度是常数级的，知道下标就可以获取或者设置存取位置的值，但是插入和删除的时间复杂度是线性级的，原因要移动元素。还有实现了可克隆接口，以及序列化接口，也就是说arraylist可以用于对象传输，其次继承了AbstractList接口，里面有一些需要实现的增删改查方法，LinkedList实现了List接口，它也是有序集合，其次还实现双端队列接口，可以充当栈或者队列，但如果用于栈或者队列，ArrayDeque性能更好。LinkedList是双向链表。如果存取的null值，那么头尾指针都指向null,每个node节点头尾都有双向引用，引用也就是c/c++所谓的指针。
* 然后说实现方法，arrayList的添加，是先判断容器是否容量足够，初始化是10个，注意的是，容量（capacity）跟集合的元素个数（size）不是一个概念，容量一般是大于元素个数的，如果容量不够，则会自动扩容，也就是大概扩容50%左右，不能说肯定扩容50%，公式是：新容量 = 原容量 + （原容量 >> 1）,然后调用工具类Arrays的copyof方法，实现数组之间的复制，而copyof方法调用的是System的copyof方法，System再调就是c/c++库了，我没办法看到System的具体实现方法。其实arrayList的remove方法也很巧妙，也是通过数组之间的复制来实现元素的删除的，具体的我忘了，而linkedList的add方法，只是修改前后两个元素的引用。删除方法同理，也就是改引用，arraylist则可能是移动元素的位置。而set和get方法，arraylist直接通过下标获取或者修改相应的值，linkedlist则需要从头或者尾开始遍历，至于从哪里开始遍历，它也是有公式的，就是比较index和（size >> 1）大小，集合右移一位，即接近中间值的值，如果索引比中间值大，则从尾部开始遍历，如果索引比中间值小，则从头部开始遍历。
* 最后还有一些判断的方法，contain方法调用的是indexof方法，indexof方法里面也是遍历集合加判断元素是否存在的，不存在则返回-1。

反射中，Class.forName和ClassLoader区别  
String，Stringbuffer，StringBuilder的区别？   
有没有可能2个不相等的对象有相同的hashcode   
简述NIO的最佳实践，比如netty，mina   
TreeMap的实现原理   

### JVM相关   

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

### JUC/并发相关   

ThreadLocal用过么，原理是什么，用的时候要注意什么   
Synchronized和Lock的区别   
synchronized 的原理，什么是自旋锁，偏向锁，轻量级锁，什么叫可重入锁，什么叫公平锁和非公平锁   
concurrenthashmap具体实现及其原理，jdk8下的改版   
用过哪些原子类，他们的参数以及原理是什么   
cas是什么，他会产生什么问题（ABA问题的解决，如加入修改次数、版本号）   
如果让你实现一个并发安全的链表，你会怎么做   
简述ConcurrentLinkedQueue和LinkedBlockingQueue的用处和不同之处   
简述AQS的实现原理    
countdowlatch和cyclicbarrier的用法，以及相互之间的差别?   
concurrent包中使用过哪些类？分别说说使用在什么场景？为什么要使用？  
LockSupport工具   
Condition接口及其实现原理   
Fork/Join框架的理解   
jdk8的parallelStream的理解   
分段锁的原理,锁力度减小的思考    

## Spring   

Spring AOP与IOC的实现原理   
Spring的beanFactory和factoryBean的区别   
为什么CGlib方式可以对接口实现代理？   
RMI与代理模式   
Spring的事务隔离级别，实现原理   
对Spring的理解，非单例注入的原理？它的生命周期？循环注入的原理，aop的实现原理，说说aop中的几个术语，它们是怎么相互工作的？  
Mybatis的底层实现原理      
MVC框架原理，他们都是怎么做url路由的   
spring boot特性，优势，适用场景等   
quartz和timer对比   
spring的controller是单例还是多例，怎么保证并发的安全   

## 分布式相关    

Dubbo的底层实现原理和机制   
描述一个服务从发布到被消费的详细过程   
分布式系统怎么做服务治理   
接口的幂等性的概念   
消息中间件如何解决消息丢失问题    
Dubbo的服务请求失败怎么处理   
重连机制会不会造成错误   
对分布式事务的理解   
如何实现负载均衡，有哪些算法可以实现？  
Zookeeper的用途，选举的原理是什么？      
数据的垂直拆分水平拆分。  
zookeeper原理和适用场景   
zookeeper watch机制   
redis/zk节点宕机如何处理    
分布式集群下如何做到唯一序列号   
如何做一个分布式锁   
用过哪些MQ，怎么用的，和其他mq比较有什么优缺点，MQ的连接是线程安全的吗   
MQ系统的数据如何保证不丢失   
列举出你能想到的数据库分库分表策略；分库分表后，如何解决全表查询的问题。   

## 算法&数据结构&设计模式   

海量url去重类问题（布隆过滤器）  
数组和链表数据结构描述，各自的时间复杂度   
二叉树遍历   
快速排序   
BTree相关的操作    
在工作中遇到过哪些设计模式，是如何应用的   
hash算法的有哪几种，优缺点，使用场景   
什么是一致性hash   
paxos算法   
在装饰器模式和代理模式之间，你如何抉择，请结合自身实际情况聊聊   
代码重构的步骤和原因，如果理解重构到模式？   

## 数据库   

MySQL InnoDB存储的文件结构   
索引树是如何维护的？   
数据库自增主键可能的问题  
MySQL的几种优化   
mysql索引为什么使用B+树   
数据库锁表的相关处理   
索引失效场景    
高并发下如何做到安全的修改同一行数据，乐观锁和悲观锁是什么，INNODB的行级锁有哪2种，解释其含义     
数据库会死锁吗，举一个死锁的例子，mysql怎么解决死锁     

## Redis&缓存相关   

Redis的并发竞争问题如何解决了解Redis事务的CAS操作吗   
缓存机器增删如何对系统影响最小，一致性哈希的实现   
Redis持久化的几种方式，优缺点是什么，怎么实现的   
Redis的缓存失效策略    
缓存穿透的解决办法   
redis集群，高可用，原理     
mySQL里有2000w数据，redis中只存20w的数据，如何保证redis中的数据都是热点数据   
用Redis和任意语言实现一段恶意登录保护的代码，限制1小时内每用户Id最多只能登录5次   
redis的数据淘汰策略   

## 网络相关   

http1.0和http1.1有什么区别   
TCP/IP协议   
TCP三次握手和四次挥手的流程，为什么断开连接要4次,如果握手只有两次，会出现什么   
TIME_WAIT和CLOSE_WAIT的区别   
说说你知道的几种HTTP响应码   
当你用浏览器打开一个链接的时候，计算机做了哪些工作步骤   
TCP/IP如何保证可靠性，数据包有哪些数据组成    
长连接与短连接     
Http请求get和post的区别以及数据包格式   
简述tcp建立连接3次握手，和断开连接4次握手的过程；关闭连接时，出现TIMEWAIT过多是由什么原因引起，是出现在主动断开方还是被动断开方。    

## 其他  

maven解决依赖冲突,快照版和发行版的区别   
Linux下IO模型有几种，各自的含义是什么  
实际场景问题，海量登录日志如何排序和处理SQL操作，主要是索引和聚合函数的应用   
实际场景问题解决，典型的TOP K问题   
线上bug处理流程   
如何从线上日志发现问题    
linux利用哪些命令，查找哪里出了问题（例如io密集任务，cpu过度）   
场景问题，有一个第三方接口，有很多个线程去调用获取数据，现在规定每秒钟最多有10个线程同时调用它，如何做到。    
用三个线程按顺序循环打印abc三个字母，比如abcabcabc。   
常见的缓存策略有哪些，你们项目中用到了什么缓存系统，如何设计的   
设计一个秒杀系统，30分钟没付款就自动关闭交易（并发会很高）   
请列出你所了解的性能测试工具   
后台系统怎么防止请求重复提交？   
有多个相同的接口，我想客户端同时请求，然后只需要在第一个请求返回结果的时候返回给客户端     
