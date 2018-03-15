## java基础   

#### 1.Arrays.sort实现原理和Collections.sort实现原理.
答：Arrays.sort()方法，如果数组长度大于等于286且连续升序和连续降序性好的话，就用归并排序，如果大于等于286且连续性不好的话就用双轴快速排序。如果长度小于286且大于等于47的话就用双轴快速排序，如果长度小于47的话就用插入排序.而Collections.sort实际上就是通过toArray方法转换成数组，然后调用TimSort方法，而不会调用LegacyMergeSort方法，即传统归并方法，而TimSort方法的核心思想就是找到数组中的有序子数组，将无序的单独出来排序，最后通过binarysort方法归并合成一个新数组，通过asList转换成集合返回。
#### 2、foreach和while的区别(编译之后)  
答：foreach 一次读取全部内容，while读一次显示一次，对于大数据量的操作建议使用while。 
#### 3、线程池的种类，区别和使用场景   
答：常见的有四种，这四种都是通过Executors静态工厂创建的，newCacheThreadPool,核心线程数初始化为0，最大线程数为Integer的最大值，非核心空闲线程的存活时间为60s,队列使用的是零缓存队列，任务来的时候直接给线程执行，不会阻塞,使用场景：执行时间短的异步任务。newFixThreadPool核心线程数等于最大线程数，其实最大线程数并没有多大作用，因为队列使用了LinkedBlockingQueue无界队列，所以当核心线程数小于任务数的时候，没有被执行的任务全部放在队列里，如果任务量足够大，就可能撑爆内存，非核心空闲线程的存活时间为0s，使用场景：执行时间长的任务,newScheduledThreadPool，周期线程池，corePoolSize为传递来的参数，maximumPoolSize为Integer.MAX_VALUE；keepAliveTime为0s,workQueue为：new DelayedWorkQueue() 一个按超时时间升序排序的队列,使用场景为周期性执行的任务，newSingleThreadExecutor，核心线程数和最大线程数都为一，非核心空闲线程的存活时间为0s,队列使用的是LinkedBlockingQueue无界队列，使用场景为一个一个的任务要有序执行。
#### 4、分析线程池的实现原理和线程的调度过程
答：线程池有什么好处？我们为什么要用线程池？线程的创建和销毁是很耗资源的，这就希望线程能够被很好的管理、复用。所以线程池的作用就出来了，分别是：减少资源的损耗;提高响应速度,避免了线程创建和销毁不要的时间;方便统一管理线程。<br/>
线程池的实现原理和调度线程的过程：线程池的创建，会初始化5到7个参数（核心线程数，最大线程数，线程存活时间，存活时间单位，队列类型，拒绝策略类型（四种：抛异常、默认会选择这种策略；直接丢弃提交过来的任务、不处理；直接让调用方调用，不经过线程池；丢弃最老的任务，也就是队列的头元素，执行提高过来的第一个任务），线程工厂）。</br>
线程池的执行（任务）：也就是线程池的实现原理，可以调用submit或者execute方法，submit方法底层就是调用execute方法，不过submit方法会返回一个FatureTask对象，而execute不会有返回值，这个对象调用get方法如果返回值是null,则说明任务执行完成。而execute方法的底层是怎么实现的呢？任务提交过来的时候(先打住一下，任务其实就是一个Runnable,执行线程只不过封装了这个任务。执行的时候调用runWorker方法,也就是runworker方法调用了任务的run方法，回来再细说这个worker类和runworker方法。),先判断任务数是否小于核心线程数，如果小于核心线程数，那么就创建一个线程扔到线程池中（如何创建？就是调用addWorker方法，回头再细说），如果大于核心线程数，并且队列没有满（我们先假设是有界队列），那么将任务存放到队列中，如果队列满了，但任务数没有超过最大线程数，那么就创建一个线程，放到线程池（HashSet存放一个个Worker，执行线程）中，如果任务超过最大线程数，那么就进行四种拒绝策略的其中一种，默认是抛异常。</br>
线程池的关闭：有两种关闭线程池的方法，分别是shutdown和shutdownNow方法。区别在哪里呢？shutdown方法分两步，先是设置线程的状态为shutdown,这个值是0向右移动29高位，这种状态会让执行线程不接受提交过来的任务，但是会执行队列里及之前的任务；然后是对空闲线程执行interrupt方法。如何判断是否为空闲非核心线程呢？空闲非核心线程一般不会上锁的，核心线程会上锁，所以只要获取是否已经上锁便可知道该线程是否为空闲非核心线程。什么是空闲非核心线程呢？空闲非核心线程就是worker的task属性为null,也可以理解为没有任务去执行，但是还存活在线程池中的非核心线程，我们知道核心线程一般不会被销毁，除非调用shutdownNow方法或者设置允许核心线程超时为true，这时候就可能被销毁。而shutdownNow方法是先设置线程状态（有五种，这个下面细说）为stop，stop的值为1先左移动29位，这种状态线程不接受提交过来的任务，并立即不处理所有的任务。然后让线程池里所有的执行线程调用interrupt方法。</br>
上面已经说完线程池的实现原理和线程的调度过程，下面主要讲一下细节上的:</br>
先说worker类，worker类里面的封装有task任务，执行线程，状态为负一，表示上锁，其他线程获取不到这个worker，还有一个run方法，里面调用了task的run方法。</br>
addworker方法，主要的是判断线程池的状态，线程池的容量及核心数，如果线程池的状态为Running，并且任务数小于线程池的容量或者小于线程数的核心线程数，那么就是让workers,也就是hashSet里面add一个worker。</br>
runWorker方法，获取worker的属性任务，然后调用任务的run方法。</br>
getTask方法，判断是都允许核心线程数超时或者当前任务数大于核心线程数，如果是，则调用阻塞队列的poll方法，否则则调用阻塞队列take方法。</br>
线程池的五种状态：分别是running(可接收提交的任务，执行线程可处理任务，调用shutdown方法进入shutdown状态，调用shutdownNow方法会进入stop状态)，shutdown(不可接收提交的任务，执行线程可处理任务，调用shutdownNow方法会进入stop状态),stop(不可接收提交的任务，执行线程不可处理任务，),tidying（当stop状态的执行线程数为null并且队列没有任务时，调用tryTerminate方法进入terminated状态）,terminated（对执行线程和队列任务会回收完毕）.

#### 5、线程池如何调优 
我们为什么要调优线程池？就是在任务并发处理的时候，让用户在获取数据少等待。一般来说，用户如果超过三秒或者是五秒之内，页面还没有完全加载完成，就会关闭页面。如果让用户有一个很好的体验，建议在2秒之内。所以性能优化方面（这里先特指后台上的线程池）必须要做足功课。线程池调优无非是设置线程池初始化时的那几个参数。</br>
核心线程数  = （并发数的范围）* 单个任务的处理时间。如果一秒钟并发的个数为500到1000个，每个任务的处理完成平均时间为0.1s,那么核心线程数设置为50到100
之间，建议设置为75个到80个。</br>
队列容量：队列常见的有无界队列、有界队列、零缓存队列等。不建议选择无界队列。因为任务进入队列会等待，而且处理的时间无法准确的把握，等待的时间就会在2s或者3s的基础上叠加。建议选择有界队列或者零缓存队列（任务不会在队列中等待，直接提交给线程处理）。如果让等待时间为1s后提交到线程中处理。那么个数和核心线程数相等。75到80之间。任务等待的时间越长，用户叠加等待的时间也就越长，建议在是使用队列的时候，队列容量越小越好。</br>
最大线程数 = （最大并发任务数-队列里的任务数）* 任务平均处理时间。如果使用零缓存队列，队列的任务数为0，最大并发任务数（每秒钟）假设为1000，任务平均处理时间为0.1s,那么最大线程数为100。当然如果硬件允许的情况下，可以设置再大一些。</br>
线程存活时间一般为60s，这个默认值大小就可以。</br>
拒绝策略：一般默认是抛拒绝执行异常，如果业务允许的话，也可以选择其中的不处理或者其他。</br>
最后较重要的是硬件方面，任务分发等。硬件不行升级硬件。任务分发不合理再重新调整权重。</br>

#### 6、线程池的最大线程数目根据什么确定   
公式：线程池的最大线程数 = （最大并发任务数 - 队列容量） * 任务平均响应时间。如果使用零缓存队列，队列容量为0.

#### 7、动态代理的几种方式（如何实现？缓存？字节码？反射机制还是fastClass机制？）
1、动态代理也称运行时增强技术，是spring框架的aop模块的实现原理，动态代理方式我了解的有：jdk动态代理，cglib动态代理，javassist动态代理。</br>

jdk动态代理的代理类必须实现invacationHandler接口，重写invoke方法，获取最终代理类是通过调用Proxy的静态方法newProxyInstance，传入三个参数，分别是目标类的类加载器，目标类的接口数组，和代理类的对象。</br>

cglib:cglib动态代理的代理类实现MethodInterceptor接口，重写intercept方法，获取最终代理类过程是创建一个增强器，然后设置目标类，设置拦截对象，最后调用增强器对象的create方法返回一个代理类。</br>

缓存：它们都将数据以key-value的形式存在缓存中，获取代理类对象一般先判断缓存中是否存在，如果不存在才通过方法反射（jdk动态代理）或者方法索引（cglib ）的方式得到。</br>

字节码底层：cglib是继承目标类和实现工厂接口，jdk动态代理是继承Proxy类和实现目标类的接口。它们最重要的相同点是最终代理类的最终目标方法会调用各自的拦截方法，invoke或者intercept方法。

反射机制还是fastClass机制：cglib的fastClass机制是对目标类的方法设置索引，然后通过索引直接调用目标类的方法。这样的好处是比jdk动态代理通过对目标类方法的反射，即调用Class.forName(xxx).getMethod(xxx)调用目标类的方法要快一些。</br>

#### 8、HashMap的并发问题  
分析地址：https://coolshell.cn/articles/9606.html </br>
当多条线程同时存取操作hashMap时，就可能会出现infinite loop (死循环)，就是当线程之间挂起和执行的链表的指向形成一个环状，就会出现死循环，这个情况出现不是特别明显，是一个隐性的bug,死循环有一个很致命的缺点，就是会让cpu飙升，最后有可能会出现宕机的情况，解决这一问题用hashtable替换或者concurrentHashMap(推荐)替换，或者是使用工具类的包装器，也就是Collections.synchronizedMap()。</br>
HashMap死循环演示</br>
假如有两个线程P1、P2，以及链表 a=》b=》null</br>
1、P1先执行，执行完"Entry<K,V> next = e.next;"代码后发生阻塞，或者其他情况不再执行下去，此时e=a，next=b</br>
2、而P2已经执行完整段代码，于是当前的新链表newTable[i]为b=》a=》null</br>
3、P1又继续执行"Entry<K,V> next = e.next;"之后的代码，则执行完"e=next;"后，newTable[i]为a《=》b，则造成回路，while(e!=null)一直死循环</br>

#### 9、了解LinkedHashMap的应用吗  
LinkedHashMap继承HashMap，也就是在HashMap的基础上进一步封装，HashMap是无序的，LinkedHashMap是有序的，因为在HashMap的基础上添加一个双向链表维护，有序迭代可分为访问顺序和插入顺序迭代，一般默认的是插入顺序迭代元素，即属性accessOrder在LinkedHashMap构造函数里设置为false，访问（调用get或者put方法）顺序的意思是：只要访问过的元素，先删除，即remove,然后add到双向链表的尾部，迭代的时候会从尾部到头部的顺序依次迭代。
#### 10、反射的原理，反射创建类实例的三种方式是什么？ 
#### 11、cloneable接口实现原理，浅拷贝or深拷贝   
#### 12、Java NIO使用   
#### 13、hashtable和hashmap的区别及实现原理，hashmap会问到数组索引，hash碰撞怎么解决?
答：以jdk1.7为示例，hashtable官方已经不推荐使用了，但是在没有concurrent包之前，hashtable还是做出了很大的贡献，首先hashtable和hashmap的底层都是hash表，数组加链表形式，hashtable线程是安全的，put和get方法都用了synchronized修饰，其次不允许存null值的，效率相对于hashmap要低一些。hashmap线程是不安全的，如果要实现线程安全，可以使用concurrentMap代替或者使用Collections工具类的synchronizeMap封装，哈希表有两种实现方式，一种是开放地址链表来实现，另一种是使用冲突链表，而hashmap使用的就是冲突链表实现的，也就是数组加链表的形式，数组一般用来存key经过hashcode方法的集合，默认容量初始化16个，注意的是容量跟元素个数大小是不一样的概念，源码告诉咋们，一般来说，容量的大小设置为元素个数除于0.75，然后加+1，为什么容量初始化设置为16个呢，原因是让数组的元素分布更加均匀一些，而不会产生更多的hash碰撞呢，如何实现更加均匀呢，就是调用hash函数，hash函数实现高位运算，也就是与运算，公式是key经过hashcode方法返回的值&（容量-1），数学公式：  index = HashCode（Key） & （Length - 1），而初始化容量最好是2次幂，这样分布的更加均匀一些，如果容量不够了，也就是元素个数超过了12个，他就会启动扩容机制，增加一倍的容量，代码是原容量左移动一位。扩容是怎么实现的呢？将原来的index重新分配，复制到新容器中，重新分配会耗相对较大的性能，所以一般建议是使用hashmap最好初始化容量，公式是：(元素个数/0.75)+1,这样是最好的，如果不确定容量，最好初始化一个相对较大的容量。put方法和get方法是怎么实现的呢？存元素的时候，先获取key,进行hashcode，得到index值，如果链表为空，则直接存入，而如果链表有元素，比较key的值，相同则覆盖，不相同则采用头插法，排在前面，entry节点的属性（hash，key，values，next，）,next属性指向旧节点。同理，get方法也是根据key，hashcode以后得到的值，找到对应的数组下标，然后通过比较key的形式获取element；数组索引就是key的hascode的值，hash碰撞就是当数组索引相同时，数据可能会存重复的情况，而hashmap一般使用链地址的方式解决，也就是索引相同的采用头插法存链表，next指针指向下一个entry.</br>
#### 14、arraylist和linkedlist区别及实现原理
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

#### 1、类的实例化顺序，比如父类静态数据，构造函数，字段，子类静态数据，构造函数，字段，他们的执行顺序   
 *   父类静态变量 --> 父类的静态代码块  --> 
    子类静态变量--> 子类的静态代码块  -->  
    父类字段  -->  父类代码块1  -->  父类代码块2（2写在1下面） --> 父类构造函数
    子类字段  -->  子类代码块1  -->  子类代码块2（2写在1下面） --> 子类构造函数

#### 2、JVM内存分代  
* 这里的内存是指的是堆内存，而不是非堆内存，堆内存（自己写的代码运行时存储的区域）和非堆内存，也称堆外内存，NIO部分就引用到堆外内存，这部分是直接和系统打交道的，当然native相关的代码也在运行时存储在这里。下面正式谈谈堆内存分代，堆内存分为新生代（三分之一）、老年代（三分之二）、还有永久代（
先不讨论永久代）。新生代里面又分eden区，from区（survivor1），to区（survivor2），对象创建一般先分到Eden区，但大对象直接分到老年代。eden区：from区：to区的空间比例是8：1：1.新生代的垃圾收集算法是复制算法，老年的垃圾收集算法是标记整理算法。当eden区的空间不足以存下新创建的对象时，会进行一次minor GC.存活的对象和survivor1区（from区）的对象复制到to区。然后清空eden区和from区。这时from区和to区进行转换，from变成to，to变成from.而存活的对象年龄加一，大于15岁这个默认值的话，就会认为是长期存活的对象，进到老年代。minor GC回收频率高，速度快。如果to区空间不足，那将触发空间担保机制。将一部分对象存到老年代，如果老年代空间也不足，那就会导致空间担保失败，进行一次Major GC .而进行一次Major GC一般会触发一次Minor GC.触发Major GC的条件还有直接调用System.gc();老年代空间不足等等。Major GC回收频率相对较小，回收时间长。

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
