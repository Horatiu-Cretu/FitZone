import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrainerOverviewComponent } from './trainer-overview.component';

describe('TrainerOverviewComponent', () => {
  let component: TrainerOverviewComponent;
  let fixture: ComponentFixture<TrainerOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrainerOverviewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TrainerOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
