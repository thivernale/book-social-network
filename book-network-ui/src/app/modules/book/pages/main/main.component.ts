import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { MenuComponent } from '../../components/menu/menu.component';

@Component({
  selector: 'app-main',
  imports: [RouterOutlet, MenuComponent],
  templateUrl: './main.component.html',
  standalone: true,
})
export class MainComponent {

}
