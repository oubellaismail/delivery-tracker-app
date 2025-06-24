import { Component } from '@angular/core';

@Component({
  selector: 'app-home',
  standalone: true,
  template: `
    <div class="home-container">
      <h1 class="animated-title">Nous sommes en train de développer une application de Delivery and Tracker App</h1>
    </div>
  `,
  styleUrls: ['./home.css']
})
export class Home {}
