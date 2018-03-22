#### 什么是ThreadLocal?
* ThreadLocal这个类位于lang包下的，专门用于解决在高并发环境下，对共享变量的访问导致线程出现不安全的问题。也就是通过线程内部副本变量的方式。
#### 那ThreadLocal内部的结构是怎么样的，如何保证线程安全的？
* ThreadLocal内部维护着一个ThreadLocalMap，这个map是ThreadLocal的静态内部类，静态内部类保证了线程访问内部类属性的安全，
里面存储的是键值对。key一般是线程的id,value就是我们想要的目的值。这样做的好处就是保证每一个线程维护自己的局部映射表。
而ThreadLocalMap是如何解决散列冲突的，也可以说我们知道的hash碰撞。我们知道，解决hash碰撞有两种方式，一种是链表的方式。
HashMap内部就是使用这种方式解决hash碰撞的，也就是对key进行hashcode以后得到的存储的地址，如果有值存在，就在值的后面添加
一条小尾巴，就是链表，下次我们可以通过遍历链表查询得到我们想要的值，而另外一种方式就是通过开放地址法，什么是开放地址法，就是
对key进行hashcode后得到的内存地址有值后，就向后位移，找到空位坐下，因为一般hashcode比较均匀，所以才采用这种方式。到了数组的
末尾还没有找到空位，就返回头部继续找空位。那这个hash值为什么会这么均匀呢？它是通过automicInteger和一个固定的值累加上去的。添加
一对key-value,automicInteger就自动加一。
#### ThreadLocal的应用场景？
* 一般是数据库连接的时候和管理Session的时候。因为数据库如何大量连接而且很多是重复的ip地址的话，数据库容易崩溃的。
Session也是。
#### ThreadLocal内存泄漏的原因以及我们应该如何解决？
*　ThreadLocal内存泄漏的原因是ThreadlocalMap是静态修饰的，生命周期和ThreadLocal一样长，如果用完没有手动删除对应的key就可能导致内存泄漏。
所以预防的策略就是要养成一个好习惯，用完之后也就是获取了之后及时remove对应key的数据。
