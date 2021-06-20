# Clean Architecture (minus Reactive Streams)

Many Android implementations of clean architecture use [Reactive Streams](http://www.reactive-streams.org/) (usually RxJava or Kotlin Flow) to connect architectural layers together. I believe that while Reactive Streams is a fantastic initiative, it is *completely the wrong abstraction* for architecting android applications.

Sounds crazy right? Well, take a look at the code, I'd love to hear back if you find a way to improve it further (for me, improvement means: quicker to develop, more robust, easier to understand and maintain, no magic, less boilerplate, thinner UI layer). These are the qualities that let a technique scale for huge mobile app projects IMO.

![screen shot of sample](clean_modules_screenshot.png)
![video of sample](clean-modules-vid.gif)

The typical android clean architecture example you'll find on the internet is only reactive in a fairly trivial way: the UI triggers a request -> then receives a response, sometimes with updates (essentially a callback).

Once you include reacting to things which aren't triggered by the UI though, things get complicated. Let's say you want your UI to react to a change of network status, or an external accessory being plugged in, or a notification being received to the device (or all of these at the same time). Add Android's infamous lifecycle into the mix, and the abstraction starts to look a little shakey.

The typical solution to this is to turn _everything_ into a reactive stream, and with enough RxJava operators you can of course fix all the caching and memory reference problems - but these are problems that _only exist_ because of choosing an inappropriate abstraction in the first place. Remove Reactive Streams from your android architecture and you remove the problems.

# How does it work

So how do you have reactive UIs without using reactive streams? In a nutshell: the observer pattern. This has been around for maybe half a century(?) and is the basis of just about every low level UI component in existence AFAIK.

In this sample, the observable pattern is implemented with a library (fore) but it's important to realise that the actual code is fairly trivial, it boils down to a list of observers (usually the observers are part of the UI layer) that implement this interface:

```
interface Observer {
    fun somethingChanged()
}
```

## Clean modules

This is how the kotlin modules are arranged in this sample (domain is implemented as a pure kotlin module)

![module structure](architecture.png)

All Android implementations of [clean architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) need to be adapted because the original clean architecture blog post leans towards server side considerations, it doesn't directly address mobile applications, or kotlin, or reactive UIs, or ViewModels, or even say much about how you should treat state. As such there are no presenters, controllers or entities in this sample, and we use the word [*model*](https://en.wikipedia.org/wiki/Domain_model) to represent domain mobel classes, and they collaborate with each other using **mediators**.

## Where are the use cases?
You might be familiar with the common implementations of clean architecture adapted for android apps that are mentioned at the top of these docs - they often use a particular form of stateless UseCase class implemented with reactive streams. If you're interested, the [use cases](https://en.wikipedia.org/wiki/Use_case) for this app can be found in the public functions of the domain models e.g. WeatherModel.fetchWeatherReport()


# License

    Copyright 2015-2021 early.co

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.