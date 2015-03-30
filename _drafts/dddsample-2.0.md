---
layout: post
title:  "DDD Sample App 2.0 Rationale"
date:   2015-03-28 12:34:40
tags: general
author: patrik_fredriksson
---

##The initial creation of DDD Sample
The initial version of the DDD Sample app was presented at QCon London 2009. The content presented was the result of many many hours of hard work, the vast majority by core contributor Peter Backlund. 

There were several reasons we originally decide to embark on this journey. At the time, the amount of material available on DDD beyond Eric Evans' original book was quite limited. While Eric's book is a comprehensive guide to Domain-Driven Design, providing many good topics on design both in the large (strategic) and in the small (tactical), there wasn't really any good guidance on how to put everything together in real full-stack applications. 

As we all know, it's not always that easy to build great software when all sorts of real-world constraints hit you. People really appreciated the DDD approach, but wanted a more comprehensive application example. Enter the sample application.

##Goals
As we reboot the DDD Sample app effort, the goals from the original project remain largely the same.

#A how-to example for implementing a typical DDD application
The overall goal of the application is to provide an example of how a typical DDD application can be implemented using tools that most developer recognize from their current work situation. We intentionally wanted to use "middle-of-the-road" technologies, thus being able to show an example that could be of most use to many developers. Being most comfortable in Java, Java was an obvious choice for us at the time. The new version of the DDD Sample app will keep Java as the core language, but updated to Java 8 we can take advantage of some of the new language features.

#Support discussions around implementation practices
By putting something out there, subject to public scrutiny, we hoped we could spark interesting discussions about implementation, practices, tools, architecture, language choices, etc. And we certainly got that, there have been many discussion in online forums, blog posts, and at conferences where the sample app has been referenced. We have also seen a number of, mostly partial, implementations in other languages and frameworks including C#, Go, Ruby, and Qi4j, to mention some. 

#Lab environment for controlled experiments
By having a somewhat well designed core and set of features, one goal was to be able to try out new ideas easily. For example:

* What happens if we replace the relational database storage with a document database like MongoDB?
* What if we replaced view technology _x_ with view technology _y_? Or server-side generated pages with a single-page rich interface in JavaScript?
* What different options for internal message-passing supporting eventual consistency can we find? What if we replace JMS with a simple thread-pool, or by ZeroMQ?
* What if we kept the feature set and domain logic, but moved to a CQRS style of architecture?

We believe that by reducing the scope and feature set of the application somewhat and drawing upon evolved ideas from the DDD community over the last few years, we can meet these goals better going forward. 

##Non-goals
The non-goals of the original DDD Sample app remains, and are worth repeating.

#(The only correct way of building a DDD application)
The DDD Sample app is not meant as a blueprint, and certainly not as the only correct way of building a DDD application. It may serve as inspiration (or deterrent), it may show examples on how to approach a particular implementation problem. But that's it. There will be many other ways of writing DDD applications, each with its own set of upsides and downsides.

#(Explore cutting-edge frameworks)
The idea of the DDD Sample app was never to show off new clever frameworks or languages. We wanted to provide an example based on commonly used tool to make it accessible and possible to integrate useful parts in other applications. That said, we also hoped to inspire reimplementation in other languages of frameworks as a humble way of stimulating innovation and knowledge sharing. We hope that reviving the sample app and also somewhat reducing it in scope can keep this discussion going as new languages, paradigms, and frameworks emerge, hopefully showing some of the clunkiness of the Java implementation in more elegant ways.  

#(A complete definition of DDD)
Domain-Driven Design is a set of driving principles on how to build software in a creative collaboration between domain practitioners and software practitioners. A sample app can not show everything involved in creating great software in a complex domain; it's rather to be seen as a snapshot, capturing the state of the software at that particular point in time. Modelling sessions, domain expert interaction, design trade offs, etc are not captured in the DDD Sample Application, thus it is not a complete definition of DDD by far, and neither does it have such an aspiration. That said, we believe we probably can do more to describe why we ended up with the design we did, e.g. providing reference scenarios for implemented features.

##Now, then?
In an upcoming post, we will go into somewhat more detail on our plan for DDD Sample Application reboot. Stay tuned!
