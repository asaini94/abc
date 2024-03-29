import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MydialogComponent } from './mydialog.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('MydialogComponent', () => {
  let component: MydialogComponent;
  let fixture: ComponentFixture<MydialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MydialogComponent ],
      schemas: [ CUSTOM_ELEMENTS_SCHEMA ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MydialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
