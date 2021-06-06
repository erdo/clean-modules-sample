# Clean Architecture & MVVM implementation of an Android application, without using reactive-streams

Many common implementations of clean architecture use reactive-streams (usually rxJava or kotlin Flow) to connect the domain layer to the ui layer. For a number of reasons, I believe that while reactive streams are great, they are **completely the wrong abstraction** for implementing typical android applications.

Sounds crazy right? Well, take a look at the code, I'd love to hear back if you find a way to improve it further (for me, improvement means: quicker to develop, more robust, easier to understand and maintain, less boilerplate, thinner UI layer).

A lot of android clean architecture examples you'll find on the internet are only reactive in a fairly trivial way: the UI triggers a request -> then receives a response (sometimes with updates). However, once you include reacting to lots of things which aren't directly triggered by the UI, things get complicated. Let's say you want your UI code to automatically update itself based on a change of network status, or an external accessory being plugged in, or a notification being received to the device (or all of these at the same time).

This is where the abstraction starts to break down, the typical solution to this is to turn *everything* into a reactive stream and with enough RxJava operators you can of course fix all the problems - but these are problems that only exist because of choosing an innapropriate abstraction in the first place. Removing reactive-streams from your android app's architecture, actually removes the problems.

![module structure](architecture.png)
![screen shot](clean_modules_screenshot.png)

# How does it work

So how do you have reactive UIs without using reactive-streams? In a nutshell: the observer pattern. This has been around for maybe half a century(?) and is the basis of just about every low level UI component in existence AFAIK. It's how a checkbox widget works: somewhere the checkbox state is set to true, that state is being "observed" by the widget itself, and when the state changes, the pixels of the checkbox are all redrawn to match that state - that's a reactive ui. And if the window is moved and the OS asks for the pixels to be redrawn? no problem, the checkbox widget's render loop just consults its current state, and sets all the correct pixel values again.

In this sample, the observable pattern is implemented with a library (fore) but it's important to realise that the actual code is fairly trivial, it boils down to a list of observers (usually the observers are part of the UI layer) that implement this interface:

```
interface Observer {
    fun somethingChanged()
}
```

All Android implementations of [clean architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) need to be adapted because the original clean architecture blog post leans towards server side considerations, it doesn't directly address mobile applications, or kotlin, or reactive UIs, or ViewModels, or even say much about how you should treat state. As such there are no presenters, controllers or entities in this sample, and we use the word [*model*](https://en.wikipedia.org/wiki/Domain_model) to represent domain mobel classes, and they collaborate with each other using **mediators**.

## Where are the usecases?
You might be familiar with the common implementations of clean architecture adapted for android apps that are mentioned at the top of these docs - they often use a particular form of stateless UseCase class implemented with reactive-streams. If you're interested, the [usecases](https://en.wikipedia.org/wiki/Use_case) for this app can be found in the public functions of the domain models e.g. WeatherModel.fetchWeatherReport()


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
