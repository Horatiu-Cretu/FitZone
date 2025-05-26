import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyTrainingSessionsComponent } from './my-training-sessions.component';

describe('MyTrainingSessionsComponent', () => {
  let component: MyTrainingSessionsComponent;
  let fixture: ComponentFixture<MyTrainingSessionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyTrainingSessionsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyTrainingSessionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
